package hotel.gestao;

import hotel.model.Hospede;

/**
 * Classe responsável pelo armazenamento e manipulação dos dados dos hóspedes em memória.
 * Utiliza um array de tamanho fixo para simular uma base de dados.
 */
public class GestaoHospedes {

    private static final int MAX_HOSPEDES = 1000; // Limite máximo de registos permitido
    private final Hospede[] hospedes;             // "Tabela" de hóspedes
    private int totalHospedes;                    // Contador real de hóspedes inseridos
    private int proximoId;                        // Gerador de ID automático para novos hóspedes

    public GestaoHospedes() {
        this.hospedes = new Hospede[MAX_HOSPEDES];
        this.totalHospedes = 0;
        this.proximoId = 1;
    }

    /**
     * Transfere os dados lidos do CSV para o array da gestão.
     * Também recalcula o próximo ID disponível para não sobrescrever IDs existentes.
     */
    public void carregarHospedes(Hospede[] hospedesCarregados, int quantidade) {
        for (int i = 0; i < quantidade && i < MAX_HOSPEDES; i++) {
            this.hospedes[i] = hospedesCarregados[i];
        }
        this.totalHospedes = Math.min(quantidade, MAX_HOSPEDES);

        // Lógica para garantir que o próximo ID seja maior que o maior ID já carregado
        this.proximoId = 1;
        for (int i = 0; i < totalHospedes; i++) {
            if (hospedes[i].getId() >= proximoId) {
                proximoId = hospedes[i].getId() + 1;
            }
        }
    }

    /**
     * Retorna uma cópia do array contendo apenas os hóspedes válidos (sem espaços vazios).
     */
    public Hospede[] listarTodos() {
        Hospede[] resultado = new Hospede[totalHospedes];
        for (int i = 0; i < totalHospedes; i++) {
            resultado[i] = hospedes[i];
        }
        return resultado;
    }

    /**
     * Procura um hóspede pelo seu identificador único.
     */
    public Hospede buscarPorId(int id) {
        for (int i = 0; i < totalHospedes; i++) {
            if (hospedes[i].getId() == id) {
                return hospedes[i];
            }
        }
        return null; // Retorna null se o ID não existir
    }

    /**
     * Procura um hóspede pelo número do documento (NIF, CC, Passaporte).
     */
    public Hospede buscarPorDocumento(String documento) {
        for (int i = 0; i < totalHospedes; i++) {
            if (hospedes[i].getDocumento().equals(documento)) {
                return hospedes[i];
            }
        }
        return null;
    }

    /**
     * Verifica se um documento já está registado no sistema.
     */
    public boolean documentoExiste(String documento) {
        return buscarPorDocumento(documento) != null;
    }

    /**
     * Cria e adiciona um novo hóspede ao sistema.
     * Valida se há espaço e se o documento já não está em uso.
     */
    public Hospede criarHospede(String nome, String documento) {
        // Validação de limite de memória
        if (totalHospedes >= MAX_HOSPEDES) {
            return null;
        }

        // Regra de negócio: Não permitir dois hóspedes com o mesmo documento
        if (documentoExiste(documento)) {
            return null;
        }

        // Instancia o novo hóspede usando o ID autoincrementado
        Hospede novo = new Hospede(proximoId++, nome, documento);
        hospedes[totalHospedes++] = novo; // Adiciona e incrementa o total
        return novo;
    }

    /**
     * Atualiza os dados de um hóspede existente.
     * Impede que o documento seja alterado para um que já pertença a outra pessoa.
     */
    public boolean editarHospede(int id, String nome, String documento) {
        Hospede hospede = buscarPorId(id);
        if (hospede == null) {
            return false;
        }

        // Se o novo documento já existe em OUTRO hóspede, cancela a edição
        if (documentoExiste(documento) && !hospede.getDocumento().equals(documento)) {
            return false;
        }

        hospede.setNome(nome);
        hospede.setDocumento(documento);
        return true;
    }

    /**
     * Retorna os dados atuais para serem gravados no ficheiro CSV.
     */
    public Hospede[] getHospedesParaSalvar() {
        Hospede[] resultado = new Hospede[totalHospedes];
        for (int i = 0; i < totalHospedes; i++) {
            resultado[i] = hospedes[i];
        }
        return resultado;
    }

    public int getTotalHospedes() {
        return totalHospedes;
    }

    public int getProximoId() {
        return proximoId;
    }
}