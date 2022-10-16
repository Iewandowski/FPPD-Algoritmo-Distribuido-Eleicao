public class App {
    public static void main(String[] args) {
        // identificar coordenador
        int coordenador = 6;

        Anel anel = new Anel();
        anel.gerarProcesso();
        anel.finalizarCoordenador(coordenador);
    }
}
