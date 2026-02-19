package hotel.menu;

import java.util.Scanner;
import hotel.App;
import hotel.gestao.*;
import hotel.model.*;

/**
 * Interface de utilizador para visualização e consulta de quartos.
 * Cruza dados com a gestão de reservas para identificar ocupantes atuais.
 */
public class MenuQuartos {

    private final Scanner scanner;
    private final GestaoQuartos gestaoQuartos;
    private final GestaoReservas gestaoReservas;
    private final GestaoHospedes gestaoHospedes;

    public MenuQuartos(Scanner scanner, GestaoQuartos gestaoQuartos,
                       GestaoReservas gestaoReservas, GestaoHospedes gestaoHospedes) {
        this.scanner = scanner;
        this.gestaoQuartos = gestaoQuartos;
        this.gestaoReservas = gestaoReservas;
        this.gestaoHospedes = gestaoHospedes;
    }

    /**
     * Ciclo principal do módulo de quartos.
     */
    public void executar() {
        while (true) {
            mostrarMenu();
            int opcao = MenuUtils.lerOpcao(scanner);

            switch (opcao) {
                case 1 -> listarQuartos(gestaoQuartos.listarTodos(), "TODOS OS QUARTOS");
                case 2 -> listarQuartos(gestaoQuartos.listarLivres(), "QUARTOS LIVRES");
                case 3 -> listarOcupados();
                case 4 -> listarQuartoEspecifico();
                case 0 -> { return; }
                default -> System.out.println(App.RED + "⚠️ Opção inválida!" + App.RESET);
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\n" + App.CYAN + "┌──────────────────────────────────────┐");
        System.out.println("│          CONTROLO DE QUARTOS         │");
        System.out.println("└──────────────────────────────────────┘" + App.RESET);
        System.out.println(" 1. " + App.YELLOW + "➔" + App.RESET + " Ver todos os quartos");
        System.out.println(" 2. " + App.YELLOW + "➔" + App.RESET + " Ver quartos " + App.GREEN + "LIVRES" + App.RESET);
        System.out.println(" 3. " + App.YELLOW + "➔" + App.RESET + " Ver quartos " + App.RED + "OCUPADOS" + App.RESET);
        System.out.println(" 4. " + App.YELLOW + "➔" + App.RESET + " Procurar quarto específico");
        System.out.println(" 0. " + App.RED + "«" + App.RESET + " Voltar");
        System.out.print("\nEscolha uma opção: ");
    }

    /**
     * Método genérico que formata a exibição de uma lista de quartos numa tabela.
     * @param titulo Texto a exibir no topo da listagem.
     */
    private void listarQuartos(Quarto[] quartos, String titulo) {
        System.out.println("\n" + App.CYAN + "📊 " + titulo + App.RESET);
        if (quartos.length == 0) {
            System.out.println(App.YELLOW + "ℹ️ Sem quartos para exibir nesta categoria." + App.RESET);
            return;
        }

        imprimirCabecalhoTabela();
        for (Quarto q : quartos) {
            // Lógica de cor condicional baseada no estado booleano isOcupado
            String statusCor = q.isOcupado() ? App.RED + "OCUPADO" : App.GREEN + "LIVRE";
            System.out.printf("%-5d | %-15s | %-10.2f€ | %-15s%n",
                    q.getNumero(), q.getTipo(), q.getPrecoDiario(), statusCor + App.RESET);
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Total: " + App.BOLD + quartos.length + App.RESET + " quarto(s)");
    }

    /**
     * Lista apenas os quartos ocupados, detalhando QUEM está lá dentro.
     * Esta é uma função de "cruzamento" (Join) entre Quartos, Reservas e Hóspedes.
     */
    private void listarOcupados() {
        Quarto[] ocupados = gestaoQuartos.listarOcupados();
        System.out.println("\n" + App.RED + "🚩 QUARTOS ATUALMENTE OCUPADOS" + App.RESET);

        if (ocupados.length == 0) {
            System.out.println("Nenhum quarto ocupado de momento.");
            return;
        }

        for (Quarto q : ocupados) {
            System.out.println(App.CYAN + "Quarto " + q.getNumero() + " (" + q.getTipo() + ")" + App.RESET);

            // Pergunta à gestão de reservas: "Quem tem uma reserva ativa para este quarto agora?"
            Reserva reservaAtual = gestaoReservas.getReservaAtualDoQuarto(q.getId());
            if (reservaAtual != null) {
                // Pergunta à gestão de hóspedes: "Qual o nome do dono desta reserva?"
                Hospede hospede = gestaoHospedes.buscarPorId(reservaAtual.getIdHospede());
                System.out.printf("   👤 Hóspede: %-20s | 📅 Até: %s%n",
                        (hospede != null ? App.BOLD + hospede.getNome() + App.RESET : "Desconhecido"),
                        reservaAtual.getDataFim());
            }
            System.out.println("   -------------------------------------------------");
        }
    }

    /**
     * Mostra a ficha técnica de um quarto e o seu histórico completo de hóspedes.
     */
    private void listarQuartoEspecifico() {
        System.out.print("\n" + App.YELLOW + "🔎 Número do quarto: " + App.RESET);
        try {
            int numero = Integer.parseInt(scanner.nextLine().trim());
            Quarto quarto = gestaoQuartos.buscarPorNumero(numero);

            if (quarto == null) {
                System.out.println(App.RED + "❌ Quarto não encontrado." + App.RESET);
                return;
            }

            // Exibe dados base do quarto
            System.out.println("\n" + App.CYAN + "=== DETALHES DO QUARTO " + numero + " ===" + App.RESET);
            System.out.println("Tipo: " + quarto.getTipo());
            System.out.println("Preço: " + quarto.getPrecoDiario() + "€");
            System.out.println("Estado: " + (quarto.isOcupado() ? App.RED + "OCUPADO" : App.GREEN + "LIVRE") + App.RESET);

            // Obtém todas as reservas (passadas, presentes e futuras) deste quarto
            Reserva[] historico = gestaoReservas.listarTodasPorQuarto(quarto.getId());
            System.out.println("\n" + App.YELLOW + "📜 Histórico de Reservas:" + App.RESET);

            if (historico.length == 0) {
                System.out.println("   (Sem registos)");
            } else {
                for (Reserva r : historico) {
                    Hospede h = gestaoHospedes.buscarPorId(r.getIdHospede());
                    System.out.printf("   • [%s a %s] - %s%n",
                            r.getDataInicio(), r.getDataFim(), (h != null ? h.getNome() : "N/A"));
                }
            }

        } catch (NumberFormatException e) {
            System.out.println(App.RED + "❌ Erro: Introduza um número válido." + App.RESET);
        }
    }

    /**
     * Helper para manter a estética de tabelas consistente.
     */
    private void imprimirCabecalhoTabela() {
        System.out.println("------------------------------------------------------------");
        System.out.printf(App.BOLD + "%-5s | %-15s | %-11s | %-15s%n" + App.RESET,
                "NUM", "TIPO", "PREÇO/DIA", "ESTADO");
        System.out.println("------------------------------------------------------------");
    }
}