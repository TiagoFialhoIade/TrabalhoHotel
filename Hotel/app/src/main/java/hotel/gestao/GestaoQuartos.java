package hotel.gestao;

import java.time.LocalDate;
import hotel.model.Quarto;
import hotel.model.Reserva;

/**
 * Classe responsável pela lógica de negócio dos quartos.
 * Gere a disponibilidade, filtros de ocupação e deteção de conflitos de reservas.
 */
public class GestaoQuartos {

    private static final int MAX_QUARTOS = 200; // Limite físico de memória para quartos
    private Quarto[] quartos;
    private int totalQuartos;

    public GestaoQuartos() {
        this.quartos = new Quarto[MAX_QUARTOS];
        this.totalQuartos = 0;
    }

    /**
     * Transfere os dados lidos do CSV para o array interno.
     */
    public void carregarQuartos(Quarto[] quartosCarregados, int quantidade) {
        if (quartosCarregados == null) return;

        for (int i = 0; i < quantidade && i < MAX_QUARTOS; i++) {
            this.quartos[i] = quartosCarregados[i];
        }
        this.totalQuartos = Math.min(quantidade, MAX_QUARTOS);
    }

    /**
     * Lista todos os quartos usando System.arraycopy para maior performance.
     */
    public Quarto[] listarTodos() {
        Quarto[] resultado = new Quarto[totalQuartos];
        System.arraycopy(quartos, 0, resultado, 0, totalQuartos);
        return resultado;
    }

    public Quarto[] listarLivres() { return filtrarPorOcupacao(false); }
    public Quarto[] listarOcupados() { return filtrarPorOcupacao(true); }

    /**
     * Método genérico para filtrar quartos por estado (livre/ocupado).
     * Segue o princípio DRY (Don't Repeat Yourself).
     */
    private Quarto[] filtrarPorOcupacao(boolean statusDesejado) {
        int count = 0;
        for (int i = 0; i < totalQuartos; i++) {
            if (quartos[i].isOcupado() == statusDesejado) count++;
        }

        Quarto[] resultado = new Quarto[count];
        int index = 0;
        for (int i = 0; i < totalQuartos; i++) {
            if (quartos[i].isOcupado() == statusDesejado) {
                resultado[index++] = quartos[i];
            }
        }
        return resultado;
    }

    public Quarto buscarPorId(int id) {
        for (int i = 0; i < totalQuartos; i++) {
            if (quartos[i].getId() == id) return quartos[i];
        }
        return null;
    }

    public Quarto buscarPorNumero(int numero) {
        for (int i = 0; i < totalQuartos; i++) {
            if (quartos[i].getNumero() == numero) return quartos[i];
        }
        return null;
    }

    /**
     * Atualiza o estado visual do quarto (bolinha verde/vermelha) com base no dia de HOJE.
     * Varre todas as reservas e verifica se a data atual está entre o início e o fim de alguma.
     */
    public void atualizarOcupacao(Reserva[] reservas, int totalReservas) {
        String hoje = LocalDate.now().toString(); // Formato YYYY-MM-DD

        for (int i = 0; i < totalQuartos; i++) {
            quartos[i].setOcupado(false); // Reset padrão: assume-se livre

            for (int j = 0; j < totalReservas; j++) {
                Reserva reserva = reservas[j];
                // Só conta se a reserva estiver ativa e pertencer a este quarto
                if (reserva.isAtiva() && reserva.getIdQuarto() == quartos[i].getId()) {
                    String inicio = reserva.getDataInicio();
                    String fim = reserva.getDataFim();

                    // Lógica: se hoje >= inicio E hoje <= fim, o quarto está ocupado AGORA
                    if (inicio.compareTo(hoje) <= 0 && hoje.compareTo(fim) <= 0) {
                        quartos[i].setOcupado(true);
                        break; // Já sabemos que está ocupado, não precisamos ver mais reservas deste quarto
                    }
                }
            }
        }
    }

    /**
     * Algoritmo de atribuição inteligente de quartos:
     * 1. Filtra quartos com capacidade suficiente.
     * 2. Verifica se NÃO há sobreposição de datas com outras reservas existentes.
     * 3. Escolhe o quarto que "sobra menos espaço" (Best Fit) para otimizar o hotel.
     */
    public Quarto encontrarQuartoAdequado(int numHospedes, Reserva[] reservas, int totalReservas,
                                          String dataInicio, String dataFim) {
        Quarto melhor = null;
        int menorDiferenca = Integer.MAX_VALUE;

        for (int i = 0; i < totalQuartos; i++) {
            Quarto quarto = quartos[i];

            // Regra 1: Capacidade
            if (quarto.getCapacidade() < numHospedes) continue;

            // Regra 2: Conflito de agenda
            boolean temConflito = false;
            for (int j = 0; j < totalReservas; j++) {
                Reserva reserva = reservas[j];
                if (reserva.isAtiva() && reserva.getIdQuarto() == quarto.getId()) {
                    if (hasSobreposicao(dataInicio, dataFim, reserva.getDataInicio(), reserva.getDataFim())) {
                        temConflito = true;
                        break;
                    }
                }
            }

            // Regra 3: Otimização (Best Fit)
            if (!temConflito) {
                int diferenca = quarto.getCapacidade() - numHospedes;
                if (diferenca < menorDiferenca) {
                    melhor = quarto;
                    menorDiferenca = diferenca;
                }
            }
        }
        return melhor;
    }

    /**
     * Lógica Matemática de Sobreposição:
     * Duas datas sobrepõem-se se (Início1 <= Fim2) E (Início2 <= Fim1).
     */
    private boolean hasSobreposicao(String inicio1, String fim1, String inicio2, String fim2) {
        return inicio1.compareTo(fim2) <= 0 && inicio2.compareTo(fim1) <= 0;
    }

    public int getTotalQuartos() {
        return totalQuartos;
    }
}
