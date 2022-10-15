public class Eleicao {

    private Processo coordenador;

    public void notificar() {
        // if coordenador == null
        // System.err.println("Coordenador nao esta respondendo");
        // else
        // System.out.println("Coordenador Atual:" + coordenador.toString() + "
        // consultado...");
    }

    public void eleger() {

    }

    public Processo getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(Processo coordenador) {
        this.coordenador = coordenador;
    }

}
