public class Gerenciador {
    private Eleicao eleicao;

    public Gerenciador() {
        eleicao = new Eleicao();

    }

    public void criar() {

    }

    public void inicializar() {
        // consulta coordenador
        // desativa coordenador por x tempo
    }

    public void encerrar() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void desativaCoordenador() {
        // Processo coordenador = eleicao.
    }
}
