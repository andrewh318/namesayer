package app.models;

/**
 * This class is a helper class that executes bash commands and reduces boiler plate code.
 * @author: Andrew Hu and Vincent Tunnell
 */
public class BashCommand {
    private String _cmd;
    private Process _process;

    public BashCommand(String cmd){
        _cmd = cmd;
    }

    /**
     * Creates and starts a new process with the command specified on construction of this object
     */
    public void startProcess(){
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", _cmd);
        pb.redirectErrorStream(true);
        try{
            _process = pb.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Process getProcess(){
        return _process;
    }
}