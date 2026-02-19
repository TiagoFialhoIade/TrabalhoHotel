package hotel.model;

import hotel.App;

/**
 * Representa a entidade Reserva no sistema.
 * Esta classe é um "contentor de dados" que guarda as informações de uma estadia.
 */
public class Reserva {
    // Atributos privados para garantir o Encapsulamento
    private int id;              // Identificador único da reserva
    private int idQuarto;        // Chave estrangeira para ligar ao Quarto
    private int idHospede;       // Chave estrangeira para ligar ao Hóspede
    private int numeroHospedes;  // Quantidade de pessoas nesta reserva
    private String dataInicio;   // Data de check-in (ISO YYYY-MM-DD)
    private String dataFim;      // Data de check-out (ISO YYYY-MM-DD)
    private boolean ativa;       // Estado da reserva (True = Ativa, False = Cancelada)

    /**
     * Construtor vazio:
     * Permite criar o objeto para depois preencher com os setters.
     */
    public Reserva() {}

    /**
     * Construtor completo:
     * Facilita a criação rápida, especialmente ao ler dados do ficheiro CSV.
     */
    public Reserva(int id, int idQuarto, int idHospede, int numeroHospedes,
                   String dataInicio, String dataFim, boolean ativa) {
        this.id = id;
        this.idQuarto = idQuarto;
        this.idHospede = idHospede;
        this.numeroHospedes = numeroHospedes;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativa = ativa;
    }

    // --- MÉTODOS DE ACESSO (GETTERS E SETTERS) ---
    // Essenciais para que outras classes (como GestaoReservas) possam ler/alterar os dados privados.

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdQuarto() { return idQuarto; }
    public void setIdQuarto(int idQuarto) { this.idQuarto = idQuarto; }

    public int getIdHospede() { return idHospede; }
    public void setIdHospede(int idHospede) { this.idHospede = idHospede; }

    public int getNumeroHospedes() { return numeroHospedes; }
    public void setNumeroHospedes(int numeroHospedes) { this.numeroHospedes = numeroHospedes; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    /**
     * Sobrescrita do método toString:
     * Define como a reserva aparece quando fazes System.out.println(reserva).
     * Usa constantes da classe App para aplicar cores (ANSI escape codes).
     */
    @Override
    public String toString() {
        // Operador ternário para definir a cor e o símbolo do status
        String status = ativa ? App.GREEN + "● ATIVA" : App.RED + "○ CANCELADA";

        // String.format ajuda a alinhar as colunas na consola (%-3d, %-2d, etc.)
        return String.format("%s[#%03d]%s Quarto: %-3d | Hóspedes: %-2d | Período: %s a %s | %s%s",
                App.BOLD, id, App.RESET,
                idQuarto,
                numeroHospedes,
                dataInicio,
                dataFim,
                status,
                App.RESET);
    }
}