package hotel.menu;

import java.util.Scanner;
import hotel.gestao.GestaoHospedes;
import hotel.model.Hospede;
import hotel.App;

/**
 * Interface de utilizador para gestão de hóspedes.
 * Permite visualizar, pesquisar e atualizar dados cadastrais.
 */
public class MenuHospedes {

    private final Scanner scanner;
    private final GestaoHospedes gestaoHospedes;

    public MenuHospedes(Scanner scanner, GestaoHospedes gestaoHospedes) {
        this.scanner = scanner;
        this.gestaoHospedes = gestaoHospedes;
    }

    /**
     * Ciclo de execução do menu de hóspedes.
     */
    public void executar() {
        while (true) {
            mostrarMenu();
            int opcao = MenuUtils.lerOpcao(scanner);

            switch (opcao) {
                case 1 -> listarTodos();
                case 2 -> procurarPorDocumento();
                case 3 -> editarHospede();
                case 0 -> { return; } // Retorna ao Menu Principal
                default -> System.out.println(App.RED + "⚠️ Opção inválida!" + App.RESET);
            }
        }
    }

    /**
     * Exibe o cabeçalho estilizado do menu.
     */
    private void mostrarMenu() {
        System.out.println("\n" + App.CYAN + "┌──────────────────────────────────────┐");
        System.out.println("│          GESTÃO DE HÓSPEDES          │");
        System.out.println("└──────────────────────────────────────┘" + App.RESET);
        System.out.println(" 1. " + App.YELLOW + "➔" + App.RESET + " Listar todos os hóspedes");
        System.out.println(" 2. " + App.YELLOW + "➔" + App.RESET + " Procurar por documento");
        System.out.println(" 3. " + App.YELLOW + "➔" + App.RESET + " Editar hóspede");
        System.out.println(" 0. " + App.RED + "«" + App.RESET + " Voltar ao menu principal");
        System.out.print("\nEscolha uma opção: ");
    }

    /**
     * Apresenta todos os hóspedes num formato de tabela alinhada.
     */
    private void listarTodos() {
        Hospede[] hospedes = gestaoHospedes.listarTodos();

        System.out.println("\n" + App.CYAN + "📋 LISTAGEM DE HÓSPEDES" + App.RESET);

        if (hospedes.length == 0) {
            System.out.println(App.YELLOW + "ℹ️ Nenhum hóspede registado no sistema." + App.RESET);
            return;
        }

        // Cabeçalho da tabela com larguras fixas (%-5s, %-25s, etc)
        System.out.println("------------------------------------------------------------");
        System.out.printf(App.BOLD + "%-5s | %-25s | %-15s%n" + App.RESET, "ID", "NOME", "DOCUMENTO");
        System.out.println("------------------------------------------------------------");

        for (Hospede h : hospedes) {
            // Imprime cada linha respeitando o alinhamento do cabeçalho
            System.out.printf("%-5d | %-25s | %-15s%n", h.getId(), h.getNome(), h.getDocumento());
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("Total: " + App.GREEN + hospedes.length + App.RESET + " hóspede(s)");
    }

    /**
     * Pesquisa um hóspede específico através do NIF/CC/Passaporte.
     */
    private void procurarPorDocumento() {
        System.out.print("\n" + App.YELLOW + "🔍 Introduza o documento: " + App.RESET);
        String documento = scanner.nextLine().trim();

        if (documento.isEmpty()) {
            System.out.println(App.RED + "❌ Erro: O documento não pode estar vazio." + App.RESET);
            return;
        }

        Hospede hospede = gestaoHospedes.buscarPorDocumento(documento);
        if (hospede == null) {
            System.out.println(App.RED + "❌ Nenhum hóspede encontrado com o documento: " + documento + App.RESET);
        } else {
            exibirDetalhesHospede(hospede);
        }
    }

    /**
     * Permite alterar os dados de um hóspede existente.
     * Implementa a lógica de "Enter para manter o atual", facilitando a UX.
     */
    private void editarHospede() {
        System.out.print("\n" + App.YELLOW + "📝 ID do hóspede a editar: " + App.RESET);
        String entrada = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(entrada);
            Hospede hospede = gestaoHospedes.buscarPorId(id);

            if (hospede == null) {
                System.out.println(App.RED + "❌ Erro: Hóspede com ID " + id + " não existe." + App.RESET);
                return;
            }

            System.out.println("\nDados atuais: " + App.CYAN + hospede + App.RESET);
            System.out.println("(Deixe em branco para manter o valor atual)");

            // Edição do Nome: se o input for vazio, preserva o valor que já estava no objeto
            System.out.print("Novo nome [" + hospede.getNome() + "]: ");
            String novoNome = scanner.nextLine().trim();
            if (novoNome.isEmpty()) novoNome = hospede.getNome();

            // Edição do Documento
            System.out.print("Novo documento [" + hospede.getDocumento() + "]: ");
            String novoDoc = scanner.nextLine().trim();
            if (novoDoc.isEmpty()) novoDoc = hospede.getDocumento();

            // Validação de Duplicados: impede que um hóspede mude o documento para um
            // que já pertença a outra pessoa no sistema.
            if (!novoDoc.equals(hospede.getDocumento()) && gestaoHospedes.documentoExiste(novoDoc)) {
                System.out.println(App.RED + "❌ Erro: Já existe outro hóspede com o documento " + novoDoc + App.RESET);
                return;
            }

            // Gravação das alterações na camada de gestão
            if (gestaoHospedes.editarHospede(id, novoNome, novoDoc)) {
                System.out.println(App.GREEN + "✅ Hóspede atualizado com sucesso!" + App.RESET);
            }

        } catch (NumberFormatException e) {
            System.out.println(App.RED + "❌ Erro: Por favor, introduza um ID numérico válido." + App.RESET);
        }
    }

    /**
     * Helper visual para mostrar os dados de um único hóspede.
     */
    private void exibirDetalhesHospede(Hospede h) {
        System.out.println("\n" + App.GREEN + "⭐ HÓSPEDE ENCONTRADO" + App.RESET);
        System.out.println("   ID: " + h.getId());
        System.out.println("   Nome: " + h.getNome());
        System.out.println("   Documento: " + h.getDocumento());
    }
}