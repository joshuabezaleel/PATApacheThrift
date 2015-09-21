package if4031;

import java.util.Scanner;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class ChatClient {
    public static void main(String [] args) {
        try {
            TTransport transport;
            transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ChatService.Client client = new ChatService.Client(protocol);
            perform(client);
            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }
    private static void perform(ChatService.Client client) throws TException
    {
        boolean exit=false;
        Scanner cli = new Scanner(System.in);
        String userName=null;
        System.out.println("Mulai dengan mengetikkan /NICK <username>");
        while(!exit){
            String userInput = cli.nextLine();
            String userAction = userInput.substring(0,userInput.indexOf(' '));
            String userMessage = userInput.substring(userInput.indexOf(' ')+1);
            System.out.println("Action : "+userAction+"\n"+"Message : "+userMessage);
            
            if(userAction.equals("/NICK")){
                if(userMessage.equals(userInput)){
                    //berarti tidak ada input <username>
                    userName = client.anonNick();
                    System.out.println("Anda login sebagai : "+ userName);
                }
                else{
                    //ada input
                    if(client.nick(userMessage)){
                        userName = userMessage;
                        System.out.println("Anda login sebagai : "+ userMessage);
                    }
                    else{
                        //nama yang sama sudah login
                        System.out.println("Sudah ada yang login dengan "+ userMessage+".\nSilahkan coba login dengan nama lain");
                    }
                }
            }
            else if(userName != null){
                if(userAction.equals("/JOIN") && !userMessage.equals("")){
                    client.join(userName, userMessage);
                }
                else if(userAction.equals("/LEAVE") && !userMessage.equals("")){
                    client.leave(userName,userMessage);
                }
                else if(userAction.equals("/EXIT")){
                    client.exit(userName);
                    exit = true;
                }
                else if(userAction.charAt(0) == '@'){
                    client.send(userMessage,userAction.substring(1));
                }
                else{ //send broadcast message
                    client.send(userMessage,null);
                }
            }
        }
    }
}
