import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Principal {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        // 1. Aquí creamos nuestro "Libro Diario" para el historial
        List<String> historial = new ArrayList<>();
        int opcion = 0;

        while (opcion != 9) {
            System.out.println("""
                    ***************************************************
                    Sea bienvenido/a al Conversor de Moneda =]
                    
                    1) Dólar =>> Peso argentino
                    2) Peso argentino =>> Dólar
                    3) Dólar =>> Real brasileño
                    4) Real brasileño =>> Dólar
                    5) Dólar =>> Guaraní paraguayo (NUEVO)
                    6) Dólar =>> Euro (NUEVO)
                    7) Euro =>> Dólar (NUEVO)
                    8) Ver Historial de Conversiones
                    9) Salir
                    
                    Elija una opción válida:
                    ***************************************************
                    """);

            opcion = teclado.nextInt();

            if (opcion >= 1 && opcion <= 7) {
                System.out.println("Ingrese el valor que deseas convertir:");
                double cantidad = teclado.nextDouble();

                switch (opcion) {
                    case 1: realizarConversion("USD", "ARS", cantidad, historial); break;
                    case 2: realizarConversion("ARS", "USD", cantidad, historial); break;
                    case 3: realizarConversion("USD", "BRL", cantidad, historial); break;
                    case 4:   realizarConversion("BRL", "USD", cantidad, historial); break;
                    case 5: realizarConversion("USD", "PYG", cantidad, historial); break;
                    case 6: realizarConversion("USD", "EUR", cantidad, historial); break;
                    case 7: realizarConversion("EUR", "USD", cantidad, historial); break;
                }
            } else if (opcion == 8) {
                // 2. Imprimimos el historial guardado
                System.out.println("\n--- HISTORIAL DE CONVERSIONES ---");
                if (historial.isEmpty()) {
                    System.out.println("Aún no hay conversiones registradas.");
                } else {
                    for (String registro : historial) {
                        System.out.println(registro);
                    }
                }
                System.out.println("---------------------------------\n");
            } else if (opcion == 9) {
                System.out.println("Cerrando el programa. ¡Gracias por usar el conversor!");
            } else {
                System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
        teclado.close();
    }

    // Le pasamos la lista 'historial' al método para que pueda agregar nuevos registros
    public static void realizarConversion(String monedaBase, String monedaDestino, double cantidad, List<String> historial) {
        String apiKey = "TU_API_KEY_AQUI";
        String direccion = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + monedaBase + "/" + monedaDestino;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(direccion))
                    .build();
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            double tasaDeConversion = jsonObject.get("conversion_rate").getAsDouble();
            double resultado = cantidad * tasaDeConversion;

            System.out.println("El valor " + cantidad + " [" + monedaBase + "] corresponde al valor final de =>>> " + resultado + " [" + monedaDestino + "]\n");

            // 3. --- LÓGICA DE MARCA DE TIEMPO ---
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaFormateada = ahora.format(formato);

            // Creamos el texto del registro y lo guardamos en la lista
            String registro = "[" + fechaFormateada + "] Convertido: " + cantidad + " " + monedaBase + " -> " + resultado + " " + monedaDestino;
            historial.add(registro);

        } catch (Exception e) {
            System.out.println("Ocurrió un error al intentar realizar la conversión: " + e.getMessage());
        }
    }
}