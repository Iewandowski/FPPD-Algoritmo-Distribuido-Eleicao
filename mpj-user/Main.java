import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import mpi.*;

public class Main {

    public static final int TAG = 99;

    public static void formatPrint(int rank, int[] valuesRecv, int[] valuesSend, boolean recv) {
        if (recv) {
            System.out.println("Process: " + rank + "; Status: " + (valuesRecv[2] == 0 ? "NORMAL" : "ELECTION") +
             "; Recv Message Value: " + valuesSend[1] + "; Counter: " + valuesRecv[0]);
        } else {
            System.out.println("Process: " + rank + "; Status: " + (valuesRecv[2] == 0 ? "NORMAL" : "ELECTION") +
             "; Send Message Value: " + valuesSend[1] + "; Counter: " + valuesRecv[0]);
        }
    }

    public static void electionPrint(int[] valuesRecv) {
        System.out.println("Process: " + valuesRecv[1] + "; Status: " + (valuesRecv[2] == 0 ? "NORMAL" : "ELECTION") +
                           "; Election Begin In: " + (valuesRecv[3] == -1 ? "NONE" : valuesRecv[3]) +
                           "; Biggest Rank: " + (valuesRecv[4] == -1 ? "NONE" : valuesRecv[4]) +
                           "; Current Coord.: " + (valuesRecv[5] == -1 ? "NONE" : valuesRecv[5]));
    }

    public static void main(String[] args) throws Exception {
        
        /* Mensagem
         * --------------------------
         * Pos  | Significado
         * --------------------------
         * 0    | Contador de voltas
         * 1    | Id processo atual
         * 2    | Status (NORMAL = 0; ELEICAO = 1)
         * 3    | Id processo que iniciou a eleição (-1 se status NORMAL)
         * 4    | Verifica maior rank para eleição (-1 se status NORMAL)
         * 5    | Id processo Coord. (-1 se não houver)
         * 6    | Processo 0 ativo ? 1 desativado e 0 ativado
         * 7    | Processo 1 ativo ? 1 desativado e 0 ativado
         * 8    | Processo 2 ativo ? 1 desativado e 0 ativado
         * 9    | Processo 3 ativo ? 1 desativado e 0 ativado
         * 10   | Processo 4 ativo ? 1 desativado e 0 ativado
         * --------------------------
         */

        int [] message_send = new int[] {12, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0};
        int [] message_recv = new int[] {0,  0, 0,  0,  0,  0, 0, 0, 0, 0, 0};
        
        int [] hasSent = new int[] {0, 0, 0, 0, 0}; // 1 se enviou, 0 se não enviou
        int [] needToRecv = new int[] {0, 0, 0, 0, 0}; // 1 se precisa receber, 0 se não precisa receber

        int [] desactivatedProcesses = new int[] {0, 0, 0, 0, 0}; // 1 se processo desativado, 0 se processo ativado
        
        int next;
        int prev;
        
        MPI.Init(args);

        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int currentSize = size;

        /* Calculate the rank of the next process in the ring. */

        next = (myrank + 1) % size;
        prev = (myrank + size - 1) % size;
        
        /* Inicia pelo processo 1 */
        if (myrank == 0) {
            System.out.println("Process " + myrank + " sending initial message to process " + next + " (" + (size) + " processes in ring)");
            MPI.COMM_WORLD.Send(message_send, 0, message_send.length, MPI.INT, next, TAG);

            needToRecv[next] = 1;
        }

        while (true) {

            MPI.COMM_WORLD.Recv(message_recv, 0, message_recv.length, MPI.INT, prev, TAG);

            needToRecv[myrank] = 0;
            hasSent[prev] = 0;

            System.out.println("Process: " + myrank + "; Need to Recv: " + Arrays.toString(needToRecv) + "; Has sent: " + Arrays.toString(hasSent));

            formatPrint(myrank, message_recv, message_send, true);

            // Contador
            if (myrank == 0) {
                --message_recv[0];
            }

            // Trigger para desativar um processo
            if (message_recv[0] == 7 && message_recv[5] != -1) {

                message_recv[message_recv[5] + 6] = 1; // Desativa processo coordanador
                message_recv[5] = -1; // Remove Id do coord. atual
                message_recv[2] = 1; // Altera status de NORMAL para ELEICAO
            }

            // Trigger para ativar um processo que esteja morto
            if (message_recv[0] == 4) {
                int deadProcessId = 0;

                for (int i = message_recv.length - 1; i >= 6; i--) {
                    if (message_recv[i] == 1) deadProcessId = (i - 6);
                }

                message_recv[deadProcessId + 6] = 0; // Ativa menor processo morto
                message_recv[5] = -1; // Remove Id do coord. atual
                message_recv[2] = 1; // Altera status de NORMAL para ELEICAO
            }

            // Trigger para iniciar Eleição
            if (message_recv[0] % 5 == 0 && message_recv[5] == -1) { // Se trigger para eleição e não houver coordenador
                message_recv[2] = 1; // Altera status de NORMAL para ELEICAO
            }

            // Computa eleição
            if (message_recv[2] == 1 && message_recv[3] == -1) { // Se status ELEICAO e for primeiro processo
                message_recv[3] = myrank; // Atualiza processo que iniciou a eleicao
                if (message_recv[4] <= myrank && message_recv[myrank + 6] == 0) message_recv[4] = myrank; // Computa rank para verificar o maior
                electionPrint(message_recv);
            } else if (message_recv[2] == 1 && message_recv[3] != myrank) { // Se status ELEICAO e for qualquer processo
                if (message_recv[4] <= myrank && message_recv[myrank + 6] == 0) message_recv[4] = myrank; // Computa rank para verificar o maior
                electionPrint(message_recv);
            } else if (message_recv[2] == 1 && message_recv[3] == myrank) { // Se status ELEICAO e for último processo
                message_recv[5] = message_recv[4]; // Adiciona novo processo coordenador
                message_recv[2] = 0; // Altera status para NORMAL
                message_recv[4] = -1;
                message_recv[3] = -1;
                electionPrint(message_recv);
            }

            TimeUnit.SECONDS.sleep(1);
            
            message_send = message_recv;
            message_send[1] = myrank;

            formatPrint(myrank, message_recv, message_send, false);
            
            MPI.COMM_WORLD.Send(message_send, 0, message_send.length, MPI.INT, next, TAG);

            needToRecv[next] = 1;
            hasSent[myrank] = 1;

            System.out.println("Process: " + myrank + "; Need to Recv: " + Arrays.toString(needToRecv) + "; Has sent: " + Arrays.toString(hasSent));

            if (message_send[0] == 0) {
                System.out.println("Exiting from loop! Process: " + message_recv[0]);
                break;
            }

        }

        if (myrank == 0) {
            formatPrint(myrank, message_recv, message_send, true);
            MPI.COMM_WORLD.Recv(message_recv, 0, message_recv.length, MPI.INT, prev, TAG);
        }

        MPI.Finalize();

        System.out.println("Process " + myrank + " done!");

    }

}
