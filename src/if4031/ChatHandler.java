/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package if4031;

import org.apache.thrift.TException;
import if4031.ChatService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ChatHandler implements ChatService.Iface{
    HashMap<String, String> users = new HashMap<String,String>();
    HashMap<String, ArrayList<String>> channels = new HashMap<String, ArrayList<String>>();
    NameGenerator  generator = new NameGenerator();
    
    @Override
    public boolean nick(String name) throws TException {
        if(users.get(name)==null){
            users.put(name, "");
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String anonNick() throws TException {
        String tmp = generator.randomIdentifier();
        users.put(tmp,"");
        return tmp;
    }

    @Override
    public boolean join(String userName, String channel) throws TException {
        if(channels.get(channel)==null){
            ArrayList<String> participant = new ArrayList<String>();
            participant.add(userName);
            channels.put(channel, participant);
        }
        else{
           channels.get(channel).add(userName);
        }
        return true;
    }

    @Override
    public boolean leave(String userName, String channel) throws TException {
       if(channels.get(channel)==null){
           return false;
       }
       else{
           channels.get(channel).remove(userName);
           return true;
       }
    }

    @Override
    public boolean exit(String userName) throws TException {
        channels.keySet().stream().forEach((tmpChannel) -> {
            while(channels.get(tmpChannel).remove(userName)){
                // needeed?
            }
        });
        users.remove(userName);
        return false;
    }

    @Override
    public boolean send(String message, String channel) throws TException {
        if(channel.equals("")){
            channels.keySet().stream().forEach((tmpChannel) -> {
                channels.get(tmpChannel).stream().forEach((name) -> {
                    String tmp = users.get(name);
                    tmp = tmp.concat(message+'\n');
                    users.replace(name, tmp);
                });
            });
        }
        else{ channels.get(channel).stream().forEach((name) -> {
            String tmp = users.get(name);
            tmp = tmp.concat(message+'\n');
            users.replace(name,tmp);
            });
        }
        return true;
    }

    @Override
    public String receive(String userName) throws TException {
        String tmp = users.get(userName);
        users.remove(userName);
        return tmp;
    }

    
    
    private class NameGenerator{        
        // For generating name, using : http://stackoverflow.com/questions/5025651/java-randomly-generate-distinct-names
        // class variable
        final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

        final java.util.Random rand = new java.util.Random();

        // consider using a Map<String,Boolean> to say whether the identifier is being used or not 
        final Set<String> identifiers = new HashSet<String>();

        public String randomIdentifier() {
            StringBuilder builder = new StringBuilder();
            while(builder.toString().length() == 0) {
                int length = rand.nextInt(5)+5;
                for(int i = 0; i < length; i++)
                    builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
                if(identifiers.contains(builder.toString())) 
                    builder = new StringBuilder();
            }
            return builder.toString();
      }
    }
}