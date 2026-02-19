package hotel.menu;

import java.util.Scanner;
import hotel.App;
import hotel.gestao.GestaoHospedes;
import hotel.gestao.GestaoQuartos;
import hotel.gestao.GestaoReservas;

/**
 * Classe central que coordena a navegação entre os diferentes módulos do sistema.
 * Atua como o "Hub" principal da aplicação.
 */
public class MenuPrincipal {

    private final Scanner scanner;
    // Sub-menus instanciados para delegar as tarefas específicas
    private final MenuQuartos menuQuartos;
    private final MenuHospedes menuHospedes;
    private final MenuReservas menuReservas;

    /**
     * Construtor do Menu Principal.
     * Recebe as instâncias de gestão para garantir que todos os sub-menus
     * partilham a mesma base de dados em memória.
     */
    public MenuPrincipal(Scanner scanner, GestaoQuartos gestaoQuartos,
                         GestaoHospedes gestaoHospedes, GestaoReservas gestaoReservas) {
        this.scanner = scanner;
        // Inicialização dos módulos específicos
        this.menuQuartos = new MenuQuartos(scanner, gestaoQuartos, gestaoReservas, gestaoHospedes);
        this.menuHospedes = new MenuHospedes(scanner, gestaoHospedes);
        this.menuReservas = new MenuReservas(scanner, gestaoQuartos, gestaoHospedes, gestaoReservas);
    }

    /**
     * Ciclo de vida principal da aplicação.
     * Mantém o programa ativo até que o utilizador escolha a opção de sair (0).
     */
    public void executar() {
        while (true) {
            mostrarMenuPrincipal();
            // Utiliza o MenuUtils para evitar crashes se o utilizador digitar texto
            int opcao = MenuUtils.lerOpcao(scanner);

            switch (opcao) {
                case 1 -> menuQuartos.executar();   // Salta para o módulo de Quartos
                case 2 -> menuHospedes.executar();  // Salta para o módulo de Hóspedes
                case 3 -> menuReservas.executar();  // Salta para o módulo de Reservas
                case 0 -> {
                    // Mensagem de encerramento antes de retornar à classe App
                    System.out.println("\n" + App.YELLOW + "📤 A encerrar módulos e a guardar base de dados..." + App.RESET);
                    return;
                }
                default -> System.out.println(App.RED + "⚠️ Opção inválida. Tente novamente." + App.RESET);
            }
        }
    }

    /**
     * Desenha a interface visual do painel de controlo no terminal.
     */
    private void mostrarMenuPrincipal() {
        System.out.println("\n" + App.CYAN + "╔════════════════════════════════════════╗");
        System.out.println("║           PAINEL DE CONTROLO           ║");
        System.out.println("╚════════════════════════════════════════╝" + App.RESET);

        System.out.println(" " + App.GREEN + "1." + App.RESET + " 🏨 Gestão de " + App.BOLD + "Quartos" + App.RESET);
        System.out.println(" " + App.GREEN + "2." + App.RESET + " 👤 Gestão de " + App.BOLD + "Hóspedes" + App.RESET);
        System.out.println(" " + App.GREEN + "3." + App.RESET + " 📅 Gestão de " + App.BOLD + "Reservas" + App.RESET);
        System.out.println(App.CYAN + "──────────────────────────────────────────" + App.RESET);
        System.out.println(" " + App.RED + "0. 👋 Sair do Sistema" + App.RESET);
        System.out.println(App.CYAN + "──────────────────────────────────────────" + App.RESET);
        System.out.print("👉 Selecione um módulo: ");
    }
}