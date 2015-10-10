/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package if40314031;

import org.apache.thrift.TException;
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
        System.out.println("nick("+name+")"); 
        System.out.println(users.size());
        System.out.println(users);
        if(!users.containsKey(name)){
            System.out.println(users.get(name));
            users.put(name, "#");
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public String anonNick() throws TException {
        System.out.println("anonNick()");
        String tmp = generator.randomIdentifier();
        users.put(tmp,"");
        return tmp;
    }

    @Override
    public boolean join(String userName, String channel) throws TException {
        System.out.println("join("+userName+","+channel+")");
        if(!channels.containsKey(channel)){
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
       System.out.println("leave("+userName+","+channel+")");
        if(!channels.containsKey(channel)){
           return false;
       }
       else{
             channels.get(channel).stream().forEach((name) -> {
                    System.out.println(name);
             });
           return channels.get(channel).remove(userName);
       }
    }

    @Override
    public boolean exit(String userName) throws TException {
        System.out.println("exit("+userName+")");
        channels.keySet().stream().forEach((tmpChannel) -> {
            while(channels.get(tmpChannel).remove(userName)){
                // needeed?
            }
        });
        users.remove(userName);
        return false;
    }
    


    @Override
    public String receive(String userName) throws TException {
        System.out.println("receive("+userName+")");
        
        if (!users.containsKey(userName)) {
            return "";
        } else {
            String tmp = users.get(userName);
            users.replace(userName,"");
            return tmp;
        }
    }

    @Override
    public boolean send(String userName, String message, String channel) throws TException {
         System.out.println("send("+userName+","+message+","+channel+")");
        if(channel.equals("")){
            channels.keySet().stream().forEach((tmpChannel) -> {
                if(channels.get(tmpChannel).contains(userName)){
                    channels.get(tmpChannel).stream().forEach((name) -> {
                        String tmp = users.get(name);
                        tmp = tmp.concat("@"+tmpChannel+" "+userName+": "+message+"\n");
                        users.replace(name, tmp);
                    });
                }
            });
        }
        else{ 
            if(!channels.get(channel).contains(userName)){
                return false;
            }
            channels.get(channel).stream().forEach((name) -> {
            
            if(!users.containsKey(name)){
                
            } else {
                String tmp = users.get(name);
                tmp = tmp.concat("@"+channel+" "+userName+": "+message+"\n");
                users.replace(name,tmp);
            }
            
            });
        }
        return true;
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