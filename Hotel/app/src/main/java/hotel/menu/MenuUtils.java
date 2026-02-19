package hotel.menu;

import java.util.Scanner;
import hotel.App;

/**
 * Utilitários para facilitar a interação com o utilizador via consola.
 */
public class MenuUtils {

    /**
     * Lê uma opção numérica da consola com tratamento de erros.
     * @param scanner O scanner ativo.
     * @return O número digitado ou -1 se a entrada for inválida.
     */
    public static int lerOpcao(Scanner scanner) {
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return -1;
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // Não imprime erro aqui para deixar o menu principal tratar a resposta
            return -1;
        }
    }

    /**
     * Limpa visualmente a consola (funciona em terminais que suportam ANSI).
     */
    public static void limparConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Pede uma confirmação ao utilizador (S/N).
     * @param scanner O scanner ativo.
     * @param mensagem A pergunta a ser feita.
     * @return true se o utilizador digitar 'S', false caso contrário.
     */
    public static boolean confirmar(Scanner scanner, String mensagem) {
        System.out.print(App.YELLOW + mensagem + " (S/N): " + App.RESET);
        String resposta = scanner.nextLine().trim().toUpperCase();
        return resposta.equals("S");
    }

    /**
     * Faz uma pausa no programa até o utilizador pressionar Enter.
     * Útil para o utilizador conseguir ler mensagens de sucesso/erro antes do menu voltar.
     */
    public static void aguardarEnter(Scanner scanner) {
        System.out.println("\nPressione " + App.BOLD + "Enter" + App.RESET + " para continuar...");
        scanner.nextLine();
    }
}