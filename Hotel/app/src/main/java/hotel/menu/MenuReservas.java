package hotel.menu;

import java.util.Scanner;
import hotel.App;
import hotel.gestao.*;
import hotel.model.*;

/**
 * Interface de utilizador para o módulo de Reservas.
 * Atua como o intermediário entre as classes de Gestão (Business Logic) e o terminal.
 */
public class MenuReservas {

    private final Scanner scanner;
    private final GestaoQuartos gestaoQuartos;
    private final GestaoHospedes gestaoHospedes;
    private final GestaoReservas gestaoReservas;

    public MenuReservas(Scanner scanner, GestaoQuartos gestaoQuartos,
                        GestaoHospedes gestaoHospedes, GestaoReservas gestaoReservas) {
        this.scanner = scanner;
        this.gestaoQuartos = gestaoQuartos;
        this.gestaoHospedes = gestaoHospedes;
        this.gestaoReservas = gestaoReservas;
    }

    /**
     * Loop principal do sub-menu de reservas.
     */
    public void executar() {
        while (true) {
            mostrarMenu();
            int opcao = MenuUtils.lerOpcao(scanner);

            switch (opcao) {
                case 1 -> criarReserva();
                case 2 -> listarTodas();
                case 3 -> listarPorQuarto();
                case 4 -> listarPorHospede();
                case 5 -> editarReserva();
                case 6 -> cancelarReserva();
                case 0 -> { return; } // Volta para o Menu Principal
                default -> System.out.println(App.RED + "⚠️ Opção inválida!" + App.RESET);
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\n" + App.CYAN + "┌──────────────────────────────────────┐");
        System.out.println("│          SISTEMA DE RESERVAS         │");
        System.out.println("└──────────────────────────────────────┘" + App.RESET);
        System.out.println(" 1. " + App.YELLOW + "➔" + App.RESET + " Criar nova reserva");
        System.out.println(" 2. " + App.YELLOW + "➔" + App.RESET + " Listar todas as reservas");
        System.out.println(" 3. " + App.YELLOW + "➔" + App.RESET + " Ver reservas por Quarto");
        System.out.println(" 4. " + App.YELLOW + "➔" + App.RESET + " Ver reservas por Hóspede");
        System.out.println(" 5. " + App.YELLOW + "➔" + App.RESET + " Editar reserva");
        System.out.println(" 6. " + App.RED + "✘" + App.RESET + " Cancelar reserva");
        System.out.println(" 0. " + App.RED + "«" + App.RESET + " Voltar");
        System.out.print("\n👉 Selecione: ");
    }

    /**
     * Fluxo guiado para criação de reserva.
     * Inclui a funcionalidade de registo rápido de hóspede caso este não exista.
     */
    private void criarReserva() {
        System.out.println("\n" + App.GREEN + "➕ NOVA RESERVA" + App.RESET);

        System.out.print("Documento do hóspede: ");
        String doc = scanner.nextLine().trim();
        if (doc.isEmpty()) {
            System.out.println(App.RED + "❌ O documento é obrigatório." + App.RESET);
            return;
        }

        // Procura o hóspede na base de dados
        Hospede hospede = gestaoHospedes.buscarPorDocumento(doc);
        if (hospede == null) {
            System.out.print(App.YELLOW + "ℹ️ Hóspede não registado. Criar novo? (S/N): " + App.RESET);
            if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
                hospede = criarNovoHospede(doc);
            } else return;
        }

        if (hospede == null) return;

        try {
            System.out.print("Quantos hóspedes para o quarto? ");
            int nHospedes = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Data Início (YYYY-MM-DD): ");
            String inicio = scanner.nextLine().trim();
            System.out.print("Data Fim    (YYYY-MM-DD): ");
            String fim = scanner.nextLine().trim();

            // Validação de formato de data estática na GestaoReservas
            if (!GestaoReservas.isDataValida(inicio) || !GestaoReservas.isDataValida(fim)) {
                System.out.println(App.RED + "❌ Erro: Formato de data incorreto." + App.RESET);
                return;
            }

            // Delegamos à GestaoQuartos a tarefa de encontrar um quarto que caiba o grupo e esteja livre nas datas
            Quarto quarto = gestaoQuartos.encontrarQuartoAdequado(nHospedes, gestaoReservas.getReservasParaSalvar(),
                    gestaoReservas.getTotalReservas(), inicio, fim);

            if (quarto == null) {
                System.out.println(App.RED + "❌ Não há quartos disponíveis com essa capacidade para as datas escolhidas." + App.RESET);
                return;
            }

            // Registo efetivo da reserva
            Reserva r = gestaoReservas.criarReserva(quarto.getId(), hospede.getId(), nHospedes, inicio, fim);
            if (r != null) {
                // Atualiza o boolean 'ocupado' dos quartos baseado na data atual do sistema
                gestaoQuartos.atualizarOcupacao(gestaoReservas.getReservasParaSalvar(), gestaoReservas.getTotalReservas());
                System.out.println(App.GREEN + "✅ Reserva efetuada com sucesso! Quarto atribuído: " + quarto.getNumero() + App.RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(App.RED + "❌ Erro: Introduza números válidos para a capacidade." + App.RESET);
        }
    }

    private void listarTodas() {
        exibirTabelaReservas(gestaoReservas.listarTodas(), "TODAS AS RESERVAS");
    }

    private void listarPorQuarto() {
        System.out.print("\nIntroduza o número do quarto: ");
        try {
            int num = Integer.parseInt(scanner.nextLine().trim());
            Quarto q = gestaoQuartos.buscarPorNumero(num);
            if (q != null) {
                exibirTabelaReservas(gestaoReservas.listarPorQuarto(q.getId()), "RESERVAS DO QUARTO " + num);
            } else System.out.println(App.RED + "❌ Quarto inexistente." + App.RESET);
        } catch (Exception e) { System.out.println(App.RED + "❌ Entrada inválida." + App.RESET); }
    }

    private void listarPorHospede() {
        System.out.print("\nDocumento do hóspede: ");
        String doc = scanner.nextLine().trim();
        Hospede h = gestaoHospedes.buscarPorDocumento(doc);
        if (h != null) {
            exibirTabelaReservas(gestaoReservas.listarPorHospede(h.getId()), "HISTÓRICO DE: " + h.getNome());
        } else {
            System.out.println(App.RED + "❌ Hóspede não encontrado." + App.RESET);
        }
    }

    /**
     * Renderiza uma tabela formatada no terminal.
     * Faz o "Join" visual entre Reserva, Quarto e Hóspede usando os respetivos IDs.
     */
    private void exibirTabelaReservas(Reserva[] reservas, String titulo) {
        System.out.println("\n" + App.CYAN + "📅 " + titulo + App.RESET);
        if (reservas == null || reservas.length == 0) {
            System.out.println(App.YELLOW + "ℹ️ Nenhuma reserva encontrada." + App.RESET);
            return;
        }

        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf(App.BOLD + "%-4s | %-6s | %-20s | %-12s | %-12s | %-8s%n" + App.RESET,
                "ID", "QUARTO", "HÓSPEDE", "INÍCIO", "FIM", "STATUS");
        System.out.println("--------------------------------------------------------------------------------------");

        for (Reserva r : reservas) {
            Quarto q = gestaoQuartos.buscarPorId(r.getIdQuarto());
            Hospede h = gestaoHospedes.buscarPorId(r.getIdHospede());
            String status = r.isAtiva() ? App.GREEN + "ATIVA" : App.RED + "CANC.";

            System.out.printf("%-4d | %-6d | %-20s | %-12s | %-12s | %-8s%n",
                    r.getId(),
                    (q != null ? q.getNumero() : 0),
                    (h != null ? truncate(h.getNome(), 20) : "N/A"),
                    r.getDataInicio(),
                    r.getDataFim(),
                    status + App.RESET
            );
        }
        System.out.println("--------------------------------------------------------------------------------------");
    }

    private void cancelarReserva() {
        System.out.print("\nID da reserva a cancelar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (gestaoReservas.cancelarReserva(id)) {
                // Ao cancelar, o quarto pode ficar livre hoje; atualizamos o estado
                gestaoQuartos.atualizarOcupacao(gestaoReservas.getReservasParaSalvar(), gestaoReservas.getTotalReservas());
                System.out.println(App.GREEN + "✅ Reserva cancelada com sucesso." + App.RESET);
            } else System.out.println(App.RED + "❌ Erro ao cancelar (ID inválido ou já cancelada)." + App.RESET);
        } catch (Exception e) { System.out.println(App.RED + "❌ ID inválido." + App.RESET); }
    }

    /**
     * Permite editar datas ou ocupantes.
     * Implementa a lógica "Enter para manter" para facilitar a UX.
     */
    private void editarReserva() {
        System.out.print("\nID da reserva a editar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Reserva r = gestaoReservas.buscarPorId(id);
            if (r == null || !r.isAtiva()) {
                System.out.println(App.RED + "❌ Reserva ativa não encontrada." + App.RESET);
                return;
            }

            System.out.println("A editar reserva de: " + App.BOLD + r.getDataInicio() + " a " + r.getDataFim() + App.RESET);

            System.out.print("Novo número de hóspedes (Enter p/ manter " + r.getNumeroHospedes() + "): ");
            String inputH = scanner.nextLine().trim();
            int nH = inputH.isEmpty() ? r.getNumeroHospedes() : Integer.parseInt(inputH);

            System.out.print("Nova data início (YYYY-MM-DD ou Enter p/ manter): ");
            String dataI = scanner.nextLine().trim();
            if (dataI.isEmpty()) dataI = r.getDataInicio();

            System.out.print("Nova data fim (YYYY-MM-DD ou Enter p/ manter): ");
            String dataF = scanner.nextLine().trim();
            if (dataF.isEmpty()) dataF = r.getDataFim();

            Quarto q = gestaoQuartos.buscarPorId(r.getIdQuarto());

            // A GestaoReservas valida se estas novas datas não atropelam outras reservas do mesmo quarto
            if (gestaoReservas.editarReserva(id, nH, dataI, dataF, q)) {
                gestaoQuartos.atualizarOcupacao(gestaoReservas.getReservasParaSalvar(), gestaoReservas.getTotalReservas());
                System.out.println(App.GREEN + "✅ Reserva atualizada com sucesso!" + App.RESET);
            } else {
                System.out.println(App.RED + "❌ Erro: Conflito de agenda ou capacidade insuficiente." + App.RESET);
            }
        } catch (Exception e) { System.out.println(App.RED + "❌ Erro ao processar edição." + App.RESET); }
    }

    private Hospede criarNovoHospede(String documento) {
        System.out.print("Nome completo do hóspede: ");
        String nome = scanner.nextLine().trim();
        return gestaoHospedes.criarHospede(nome, documento);
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
}