package jhelp;

import jhelp.orm.Definition;
import jhelp.orm.Term;
import jhelp.repos.TermRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jhelp.Operation.FAILED;
import static jhelp.Operation.SUCCESS;

public class RequestHandler implements Runnable{

    private Socket clientSocket;
    private TermRepository termRepository;
    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);

    RequestHandler(Socket clientSocket,
                   TermRepository termRepository) {
        this.clientSocket = clientSocket;
        this.termRepository = termRepository;
        log.info("New client connected at {}", clientSocket.getRemoteSocketAddress());
    }

    @Override
    public void run() {

        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream ous = new ObjectOutputStream(clientSocket.getOutputStream())) {

            Operation operation = (Operation) ois.readObject();
            switch (operation) {
                case FIND:
                    find(ois, ous);
                    break;
                case ADD:
                    add(ois, ous);
                    break;
                case DELETE:
                    delete(ois, ous);
                    break;
                case EDIT:
                    edit(ois, ous);
                    break;
            }
            log.info("Client disconnected {}", clientSocket.getRemoteSocketAddress());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void find(ObjectInputStream ois, ObjectOutputStream ous) throws IOException, ClassNotFoundException {
        String term = (String) ois.readObject();
        Optional<Term> found = termRepository.findByTerm(term);
        if (found.isPresent()) {
            List<Definition> definitions = found.get().getDefinitions();
            for (Definition def : definitions)
                ous.writeObject(def.toString());
            ous.flush();
        } else{
            ous.writeObject(null);
            ous.flush();
        }
    }

    private void add(ObjectInputStream ois, ObjectOutputStream ous) throws IOException, ClassNotFoundException {
        List<String> foundDefinitions = new ArrayList<>();
        String term = (String) ois.readObject();
        String definition = (String) ois.readObject();
        Optional<Term> found = termRepository.findByTerm(term);
        if (found.isPresent()) {
            Term foundTerm = found.get();
            foundTerm.getDefinitions().forEach(d -> foundDefinitions.add(d.getDefinition()));
            if (!foundDefinitions.contains(definition)) {
                foundTerm.addDefinition(new Definition(definition));
                termRepository.saveAndFlush(foundTerm);
                ous.writeObject(SUCCESS);
            }else {
                ous.writeObject(FAILED);
            }
        } else {
            Term newTerm = new Term(term);
            newTerm.addDefinition(new Definition(definition));
            termRepository.saveAndFlush(newTerm);
            ous.writeObject(SUCCESS);
        }
    }

    private void delete(ObjectInputStream ois, ObjectOutputStream ous) throws IOException, ClassNotFoundException {
        String term = (String) ois.readObject();
        Optional<Term> found = termRepository.findByTerm(term);
        if(found.isPresent()){
            termRepository.delete(found.get());
            ous.writeObject(SUCCESS);

        }else{
            ous.writeObject(FAILED);
        }
    }

    private void edit(ObjectInputStream ois, ObjectOutputStream ous) throws IOException, ClassNotFoundException{
        String term = (String) ois.readObject();
        String definition = (String) ois.readObject();
        Optional<Term> found = termRepository.findByTerm(term);
        if(found.isPresent()){
            Term foundTerm = found.get();
            Optional<Definition> wantedDefinition = foundTerm.getDefinitions().stream().filter(d -> d.getDefinition().equals(definition)).findFirst();
            if (wantedDefinition.isPresent()) {
                foundTerm.getDefinitions().remove(wantedDefinition.get());
                termRepository.saveAndFlush(foundTerm);
                ous.writeObject(SUCCESS);
            }
        }else{
            ous.writeObject(FAILED);
        }
    }
}
