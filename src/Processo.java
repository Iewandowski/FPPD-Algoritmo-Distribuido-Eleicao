public class Processo {
    public int id;
    public Processo anterior;
    public Processo proximo;

    public Processo(int id, Processo anterior, Processo proximo) {
        this.id = id;
        this.anterior = anterior;
        this.proximo = proximo;
    }

    public void mensagem(Processo processo) {
        if (proximo != null) {
            System.out.println("[" + this + "]" + " enviando para [" + proximo + "]");
        } else {
            System.out.println("Ganhou a eleicao");
        }
    }
}
