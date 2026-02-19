package hotel.model;

/**
 * Representa um Quarto do hotel.
 * Esta classe armazena as características físicas e o estado de ocupação atual.
 */
public class Quarto {
    // Atributos privados para controlo de acesso
    private int id;             // Identificador único (chave primária)
    private int numero;         // O número da porta do quarto
    private int capacidade;     // Lotação máxima permitida
    private boolean ocupado;    // Estado atual (calculado dinamicamente no menu)

    /**
     * Construtor padrão (vazio).
     * Útil para frameworks de serialização ou inicializações parciais.
     */
    public Quarto() {
    }

    /**
     * Construtor completo para instanciar quartos rapidamente
     * ao carregar dados do ficheiro de configuração ou base de dados.
     */
    public Quarto(int id, int numero, int capacidade, boolean ocupado) {
        this.id = id;
        this.numero = numero;
        this.capacidade = capacidade;
        this.ocupado = ocupado;
    }

    // --- GETTERS E SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    /**
     * Verifica se o quarto está ocupado no momento.
     * Importante: Este valor deve ser atualizado sempre que uma reserva começa/termina.
     */
    public boolean isOcupado() { return ocupado; }
    public void setOcupado(boolean ocupado) { this.ocupado = ocupado; }

    /**
     * Método de conveniência para a UI.
     * Retorna uma descrição textual da lotação.
     */
    public String getTipo() {
        return "Capacidade: " + capacidade;
    }

    /**
     * Lógica de negócio simples: o preço é calculado com base na capacidade.
     * Pode ser expandido para incluir tipos (Suite, Standard, etc).
     */
    public double getPrecoDiario() {
        return capacidade * 50.0; // Exemplo: 50€ por pessoa de capacidade
    }

    /**
     * Representação visual para listagens simples em consola.
     * Usa emojis para facilitar a leitura rápida do estado do quarto.
     */
    @Override
    public String toString() {
        return "Quarto " + numero + " [Capacidade: " + capacidade + "] - " +
                (ocupado ? "🔴 Ocupado" : "🟢 Livre");
    }
}