package app.models;

public class BashCommand {
    private String _cmd;
    private Process _process;

    public BashCommand(String cmd){
        _cmd = cmd;
    }

    //Creates and starts a new process with the command specified on construction of BashProcess
    public void startProcess(){
        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", _cmd);
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