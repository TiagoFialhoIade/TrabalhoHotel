package hotel.io;

import hotel.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para ler dados de ficheiros CSV.
 * Implementa lógica de fallback: tenta ler da pasta local e depois do Classpath.
 */
public class CSVReader {

    private static final String DATA_DIR = "data"; // Pasta onde o programa guarda alterações

    /**
     * Helper para abrir fluxos de leitura.
     * 1. Procura na pasta 'data' (ficheiros persistidos pelo utilizador).
     * 2. Se não existir, tenta ler do ficheiro original nos recursos do projeto.
     */
    private static BufferedReader criarBufferedReader(String nomeFicheiro) throws IOException {
        File dataFile = new File(DATA_DIR, nomeFicheiro);

        // Se o ficheiro existe na pasta 'data', abrimos esse (tem os dados mais recentes)
        if (dataFile.exists()) {
            return new BufferedReader(new FileReader(dataFile, StandardCharsets.UTF_8));
        }

        // Caso contrário, carrega o ficheiro padrão que vem dentro do .jar/classpath
        InputStream is = CSVReader.class.getClassLoader().getResourceAsStream(nomeFicheiro);
        if (is != null) {
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        }

        return null;
    }

    /**
     * Converte o CSV de quartos em objetos Quarto.
     */
    public static Quarto[] lerQuartos(String caminho) {
        List<Quarto> lista = new ArrayList<>();
        try (BufferedReader br = criarBufferedReader(caminho)) {
            if (br == null) return new Quarto[0];

            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                // Ignora o cabeçalho (ex: id,numero,capacidade...)
                if (primeiraLinha) {
                    primeiraLinha = false;
                    if (linha.toLowerCase().startsWith("id")) continue;
                }
                if (linha.trim().isEmpty()) continue; // Ignora linhas em branco

                String[] c = parseLinha(linha);
                if (c.length >= 4) {
                    lista.add(new Quarto(
                            Integer.parseInt(c[0].trim()), // ID
                            Integer.parseInt(c[1].trim()), // Número
                            Integer.parseInt(c[2].trim()), // Capacidade
                            Boolean.parseBoolean(c[3].trim()) // Ocupado
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao ler Quartos: " + e.getMessage());
        }
        return lista.toArray(new Quarto[0]);
    }

    /**
     * Converte o CSV de hóspedes em objetos Hospede.
     */
    public static Hospede[] lerHospedes(String caminho) {
        List<Hospede> lista = new ArrayList<>();
        try (BufferedReader br = criarBufferedReader(caminho)) {
            if (br == null) return new Hospede[0];

            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                if (primeiraLinha && linha.toLowerCase().startsWith("id")) {
                    primeiraLinha = false; continue;
                }
                if (linha.trim().isEmpty()) continue;

                String[] c = parseLinha(linha);
                if (c.length >= 3) {
                    lista.add(new Hospede(
                            Integer.parseInt(c[0].trim()), // ID
                            c[1].trim(),                   // Nome
                            c[2].trim()                    // Documento
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao ler Hóspedes: " + e.getMessage());
        }
        return lista.toArray(new Hospede[0]);
    }

    /**
     * Converte o CSV de reservas em objetos Reserva.
     */
    public static Reserva[] lerReservas(String caminho) {
        List<Reserva> lista = new ArrayList<>();
        try (BufferedReader br = criarBufferedReader(caminho)) {
            if (br == null) return new Reserva[0];

            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                if (primeiraLinha && linha.toLowerCase().startsWith("id")) {
                    primeiraLinha = false; continue;
                }
                if (linha.trim().isEmpty()) continue;

                String[] c = parseLinha(linha);
                if (c.length >= 7) {
                    lista.add(new Reserva(
                            Integer.parseInt(c[0].trim()), // ID
                            Integer.parseInt(c[1].trim()), // ID Quarto
                            Integer.parseInt(c[2].trim()), // ID Hospede
                            Integer.parseInt(c[3].trim()), // Num Hospedes
                            c[4].trim(),                   // Data Inicio
                            c[5].trim(),                   // Data Fim
                            Boolean.parseBoolean(c[6].trim()) // Ativa
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao ler Reservas: " + e.getMessage());
        }
        return lista.toArray(new Reserva[0]);
    }

    /**
     * Identifica automaticamente se o ficheiro usa vírgula ou ponto-e-vírgula.
     * Evita erros comuns ao abrir o CSV em diferentes versões do Excel.
     */
    private static String[] parseLinha(String linha) {
        String separador = linha.contains(";") ? ";" : ",";
        return linha.split(separador);
    }
}