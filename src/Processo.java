
public class Processo extends Thread {

    private int id;
    private Processo proximo;
    private boolean coordenador;
    private boolean ativo;

    Anel anel = Anel.getInstance();

    public Processo(int id, boolean coordenador, boolean ativo) {
        this.id = id;
        this.coordenador = coordenador;
        this.ativo = ativo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
        if (coordenador) {
            setCoordenador(false);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Processo getProximo() {
        return proximo;
    }

    public void setProximo(Processo proximo) {
        this.proximo = proximo;
    }

    public boolean isCoordenador() {
        return coordenador;
    }

    public void setCoordenador(boolean coordenador) {
        this.coordenador = coordenador;
    }

    public void requisicao(int id) {
        if (this.ativo) {
            if (!proximo.isAtivo() && proximo.coordenador) {
                System.out.println("Processo P" + this.getId() + " identificou falha no coordenador");
            }
        }
    }

}