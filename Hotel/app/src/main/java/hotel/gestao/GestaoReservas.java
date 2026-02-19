package hotel.gestao;

import java.time.LocalDate;
import hotel.model.Reserva;
import hotel.model.Quarto;

/**
 * Motor de lógica do hotel. Gere o armazenamento, criação e validação de reservas.
 * Esta classe controla quem fica onde e garante que não existem conflitos de datas.
 */
public class GestaoReservas {

    private static final int MAX_RESERVAS = 1000; // Limite físico do array
    private Reserva[] reservas;                   // Base de dados em memória
    private int totalReservas;                    // Contador de registos ocupados
    private int proximoId;                        // Auto-incremento para IDs únicos

    public GestaoReservas() {
        this.reservas = new Reserva[MAX_RESERVAS];
        this.totalReservas = 0;
        this.proximoId = 1;
    }

    /**
     * Preenche o sistema com os dados lidos do CSV.
     * Recalcula o proximoId para garantir que novas reservas não repetem IDs antigos.
     */
    public void carregarReservas(Reserva[] reservasCarregadas, int quantidade) {
        for (int i = 0; i < quantidade && i < MAX_RESERVAS; i++) {
            this.reservas[i] = reservasCarregadas[i];
        }
        this.totalReservas = Math.min(quantidade, MAX_RESERVAS);

        // Sincronização do ID: Procura o maior ID existente e soma 1
        this.proximoId = 1;
        for (int i = 0; i < totalReservas; i++) {
            if (reservas[i].getId() >= proximoId) {
                proximoId = reservas[i].getId() + 1;
            }
        }
    }

    /**
     * Filtra reservas por hóspede. Utilizado no menu de consulta de clientes.
     */
    public Reserva[] listarPorHospede(int idHospede) {
        int count = 0;
        for (int i = 0; i < totalReservas; i++) {
            if (reservas[i].getIdHospede() == idHospede) count++;
        }

        Reserva[] resultado = new Reserva[count];
        int index = 0;
        for (int i = 0; i < totalReservas; i++) {
            if (reservas[i].getIdHospede() == idHospede) {
                resultado[index++] = reservas[i];
            }
        }
        return resultado;
    }

    /**
     * Identifica a reserva que está a decorrer "neste preciso momento" num quarto.
     * Crucial para o Menu de Quartos mostrar quem é o ocupante atual.
     */
    public Reserva getReservaAtualDoQuarto(int idQuarto) {
        String hoje = LocalDate.now().toString(); // Formato YYYY-MM-DD
        for (int i = 0; i < totalReservas; i++) {
            Reserva r = reservas[i];
            // Verifica se hoje está entre a data de início e a de fim (inclusive)
            if (r.getIdQuarto() == idQuarto && r.isAtiva() &&
                    r.getDataInicio().compareTo(hoje) <= 0 &&
                    hoje.compareTo(r.getDataFim()) <= 0) {
                return r;
            }
        }
        return null;
    }

    /**
     * Retorna o histórico completo (passado, presente e futuro) de um quarto.
     */
    public Reserva[] listarTodasPorQuarto(int idQuarto) {
        int count = 0;
        for (int i = 0; i < totalReservas; i++) {
            if (reservas[i].getIdQuarto() == idQuarto) count++;
        }
        Reserva[] resultado = new Reserva[count];
        int index = 0;
        for (int i = 0; i < totalReservas; i++) {
            if (reservas[i].getIdQuarto() == idQuarto) {
                resultado[index++] = reservas[i];
            }
        }
        return resultado;
    }
    /**
     * Retorna as reservas (ativas e futuras) de um quarto específico.
     */
    public Reserva[] listarPorQuarto(int idQuarto) {
        String hoje = LocalDate.now().toString();

        // 1. Contar quantas reservas ativas existem para este quarto
        int count = 0;
        for (int i = 0; i < totalReservas; i++) {
            Reserva r = reservas[i];
            if (r.getIdQuarto() == idQuarto && r.isAtiva()) {
                count++;
            }
        }

        // 2. Criar o array de retorno
        Reserva[] resultado = new Reserva[count];
        int index = 0;
        for (int i = 0; i < totalReservas; i++) {
            Reserva r = reservas[i];
            if (r.getIdQuarto() == idQuarto && r.isAtiva()) {
                resultado[index++] = r;
            }
        }
        return resultado;
    }

    /**
     * Algoritmo de deteção de colisões (Double Booking).
     * Verifica se o intervalo de datas pedido choca com alguma reserva ATIVA já existente.
     */
    public boolean existeSobreposicao(int idQuarto, String dataInicio, String dataFim, int ignorarId) {
        for (int i = 0; i < totalReservas; i++) {
            Reserva r = reservas[i];

            // Ignora a própria reserva se estivermos em modo de edição
            if (r.getId() != ignorarId && r.getIdQuarto() == idQuarto && r.isAtiva()) {
                String rInicio = r.getDataInicio();
                String rFim = r.getDataFim();

                // Lógica Matemática: dois intervalos chocam se (Início1 <= Fim2) E (Início2 <= Fim1)
                if (dataInicio.compareTo(rFim) <= 0 && rInicio.compareTo(dataFim) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Instancia e adiciona uma nova reserva ao array.
     */
    public Reserva criarReserva(int idQuarto, int idHospede, int numHospedes, String dataInicio, String dataFim) {
        if (totalReservas >= MAX_RESERVAS) return null;
        Reserva nova = new Reserva(proximoId++, idQuarto, idHospede, numHospedes, dataInicio, dataFim, true);
        reservas[totalReservas++] = nova;
        return nova;
    }
    /**
     * Edita uma reserva existente após validar a disponibilidade e capacidade.
     */
    public boolean editarReserva(int id, int nHospedes, String dataInicio, String dataFim, Quarto quarto) {
        // 1. Procura a reserva original
        Reserva r = buscarPorId(id);

        // 2. Validações básicas: existe e está ativa?
        if (r == null || !r.isAtiva()) return false;

        // 3. Valida capacidade do quarto (se o objeto quarto for fornecido)
        if (quarto != null && nHospedes > quarto.getCapacidade()) return false;

        // 4. Valida se as novas datas não chocam com OUTRAS reservas (ignora a própria)
        if (existeSobreposicao(r.getIdQuarto(), dataInicio, dataFim, id)) return false;

        // 5. Aplica as alterações
        r.setNumeroHospedes(nHospedes);
        r.setDataInicio(dataInicio);
        r.setDataFim(dataFim);

        return true;
    }

    /**
     * Cancela uma reserva sem a apagar (Soft Delete), mantendo-a para histórico.
     */
    public boolean cancelarReserva(int id) {
        Reserva r = buscarPorId(id);
        if (r == null) return false;
        r.setAtiva(false); // Liberta o quarto para novas marcações
        return true;
    }

    /**
     * Validador de formato via Regex (Expressão Regular).
     */
    public static boolean isDataValida(String data) {
        // Verifica rigorosamente o padrão NNNN-NN-NN
        return data != null && data.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    /**
     * Retorna todas as reservas registadas.
     * Necessário para as listagens gerais no MenuReservas.
     */
    public Reserva[] listarTodas() {
        Reserva[] resultado = new Reserva[totalReservas];
        System.arraycopy(reservas, 0, resultado, 0, totalReservas);
        return resultado;
    }

    public Reserva buscarPorId(int id) {
        for (int i = 0; i < totalReservas; i++) {
            if (reservas[i].getId() == id) return reservas[i];
        }
        return null;
    }

    public int getTotalReservas() { return totalReservas; }
    public Reserva[] getReservasParaSalvar() {
        Reserva[] resultado = new Reserva[totalReservas];
        System.arraycopy(reservas, 0, resultado, 0, totalReservas);
        return resultado;
    }
}
