package hotel.model;

import hotel.App;

/**
 * Representa o cliente (Hóspede) no sistema.
 * É a entidade que realiza as reservas e é identificada pelo seu documento único.
 */
public class Hospede {
    // Atributos privados para garantir que os dados não são alterados indevidamente
    private int id;            // Identificador interno numérico (Sequencial)
    private String nome;       // Nome completo do hóspede
    private String documento;  // Documento de identificação (CC, Passaporte, etc.)

    /**
     * Construtor padrão.
     * Necessário para certas operações de leitura de ficheiros e flexibilidade inicial.
     */
    public Hospede() {
    }

    /**
     * Construtor completo.
     * Usado pela GestaoHospedes para criar novos registos rapidamente.
     */
    public Hospede(int id, String nome, String documento) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
    }

    // --- MÉTODOS DE ACESSO (GETTERS E SETTERS) ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    /**
     * Gera uma representação textual do hóspede otimizada para o terminal.
     * Utiliza cores da classe App e formatação de colunas.
     */
    @Override
    public String toString() {
        // %03d -> Garante que o ID tem 3 dígitos (ex: 001, 015)
        // %-25s -> Reserva 25 espaços para o nome, alinhando à esquerda
        return String.format("%sID: %03d%s | %-25s | Doc: %-15s",
                App.CYAN, id, App.RESET,
                nome,
                documento);
    }
}