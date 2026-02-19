package hotel.io;

import hotel.model.Hospede;
import hotel.model.Reserva;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe responsável por persistir os dados da memória para ficheiros CSV.
 * Garante que as alterações feitas durante a execução não se percam.
 */
public class CSVWriter {

    private static final String DATA_DIR = "data"; // Diretório de destino para gravação

    /**
     * Guarda a lista de hóspedes no ficheiro CSV.
     * @return true se a operação for bem-sucedida.
     */
    public static boolean guardarHospedes(String caminhoFicheiro, Hospede[] hospedes) {
        String caminhoReal = obterCaminhoEscrita(caminhoFicheiro);

        // O uso do try-with-resources garante que o BufferedWriter fecha sozinho no final
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoReal))) {

            // Escreve o cabeçalho do CSV (importante para manter a estrutura)
            bw.write("id,nome,documento");
            bw.newLine();

            for (int i = 0; i < hospedes.length; i++) {
                Hospede hospede = hospedes[i];
                // Transforma o objeto numa linha separada por vírgulas
                String linha = hospede.getId() + "," +
                        hospede.getNome() + "," +
                        hospede.getDocumento();
                bw.write(linha);
                bw.newLine();
            }

            return true;
        } catch (IOException e) {
            System.err.println("❌ Erro ao guardar ficheiro de hospedes: " + e.getMessage());
            return false;
        }
    }

    /**
     * Guarda todas as reservas (ativas e canceladas) no ficheiro CSV.
     */
    public static boolean guardarReservas(String caminhoFicheiro, Reserva[] reservas) {
        String caminhoReal = obterCaminhoEscrita(caminhoFicheiro);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoReal))) {
            // Cabeçalho completo seguindo o modelo da classe Reserva
            bw.write("id,idQuarto,idHospede,numeroHospedes,dataInicio,dataFim,ativa");
            bw.newLine();

            for (int i = 0; i < reservas.length; i++) {
                Reserva reserva = reservas[i];
                String linha = reserva.getId() + "," +
                        reserva.getIdQuarto() + "," +
                        reserva.getIdHospede() + "," +
                        reserva.getNumeroHospedes() + "," +
                        reserva.getDataInicio() + "," +
                        reserva.getDataFim() + "," +
                        reserva.isAtiva();
                bw.write(linha);
                bw.newLine();
            }

            return true;
        } catch (IOException e) {
            System.err.println("❌ Erro ao guardar ficheiro de reservas: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método auxiliar que verifica se a pasta 'data' existe.
     * Se não existir, cria a pasta antes de tentar escrever o ficheiro.
     */
    private static String obterCaminhoEscrita(String caminhoFicheiro) {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Cria a pasta data/ se necessário
        }
        // Retorna o caminho absoluto para evitar ambiguidades de diretório
        return new File(dataDir, caminhoFicheiro).getAbsolutePath();
    }
}