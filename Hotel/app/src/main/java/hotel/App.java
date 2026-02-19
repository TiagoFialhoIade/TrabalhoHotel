package hotel;

import java.util.Scanner;
import hotel.gestao.*;
import hotel.io.*;
import hotel.menu.MenuPrincipal;

public class App {
    // Configurações de Cores ANSI para o Layout
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";

    private static final String FICHEIRO_QUARTOS = "quartos.csv";
    private static final String FICHEIRO_HOSPEDES = "hospedes.csv";
    private static final String FICHEIRO_RESERVAS = "reservas.csv";

    private static GestaoQuartos gestaoQuartos = new GestaoQuartos();
    private static GestaoHospedes gestaoHospedes = new GestaoHospedes();
    private static GestaoReservas gestaoReservas = new GestaoReservas();

    public static void main(String[] args) {
        imprimirBanner();

        try (Scanner scanner = new Scanner(System.in)) {
            inicializarSistema();

            MenuPrincipal menu = new MenuPrincipal(scanner, gestaoQuartos, gestaoHospedes, gestaoReservas);

            // Hook para salvar se a consola for fechada subitamente
            Runtime.getRuntime().addShutdownHook(new Thread(App::guardarDados));

            menu.executar();

            finalizarSessao();
        } catch (Exception e) {
            System.err.println(RED + "Erro crítico no sistema: " + e.getMessage() + RESET);
        }
    }

    private static void inicializarSistema() {
        System.out.println(YELLOW + "🔄 A carregar base de dados..." + RESET);
        carregarDados();

        // Sincroniza ocupação dos quartos com base nas reservas
        var reservas = gestaoReservas.getReservasParaSalvar();
        gestaoQuartos.atualizarOcupacao(reservas, reservas.length);

        System.out.println(GREEN + "✅ Check-in do sistema concluído!" + RESET);
        System.out.printf("📊 [%d Quartos] | [%d Hóspedes] | [%d Reservas]%n%n",
                gestaoQuartos.getTotalQuartos(), gestaoHospedes.getTotalHospedes(), gestaoReservas.getTotalReservas());
    }

    private static void carregarDados() {
        try {
            // 1. Carregar Quartos
            hotel.model.Quarto[] quartosLidos = CSVReader.lerQuartos(FICHEIRO_QUARTOS);
            gestaoQuartos.carregarQuartos(quartosLidos, quartosLidos.length); // <--- MUDANÇA AQUI (usar length)

            // 2. Carregar Hóspedes
            hotel.model.Hospede[] hospedesLidos = CSVReader.lerHospedes(FICHEIRO_HOSPEDES);
            gestaoHospedes.carregarHospedes(hospedesLidos, hospedesLidos.length); // <--- MUDANÇA AQUI

            // 3. Carregar Reservas
            hotel.model.Reserva[] reservasLidas = CSVReader.lerReservas(FICHEIRO_RESERVAS);
            gestaoReservas.carregarReservas(reservasLidas, reservasLidas.length); // <--- MUDANÇA AQUI

        } catch (Exception e) {
            System.out.println(RED + "⚠️ Erro ao processar dados: " + e.getMessage() + RESET);
        }
    }

    private static void guardarDados() {
        System.out.println("\n" + YELLOW + "💾 A salvaguardar dados..." + RESET);
        boolean hOk = CSVWriter.guardarHospedes(FICHEIRO_HOSPEDES, gestaoHospedes.getHospedesParaSalvar());
        boolean rOk = CSVWriter.guardarReservas(FICHEIRO_RESERVAS, gestaoReservas.getReservasParaSalvar());

        if (hOk && rOk) {
            System.out.println(GREEN + "✨ Tudo guardado com sucesso!" + RESET);
        } else {
            System.out.println(RED + "❌ Falha ao guardar alguns dados." + RESET);
        }
    }

    private static void imprimirBanner() {
        System.out.println(CYAN + "╔════════════════════════════════════════╗");
        System.out.println("║       GRAND HOTEL MANAGEMENT           ║");
        System.out.println("╚════════════════════════════════════════╝" + RESET);
    }

    private static void finalizarSessao() {
        guardarDados();
        System.out.println(CYAN + "\n👋 Sessão terminada. Até breve!" + RESET);
    }
}