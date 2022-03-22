public class Process {
 
    private int arrivalTime;
    private int burstTime;
    private int startExecTime;
    private int finishTime;
    private int processNo;
    private int startLastBurst =0;

    public Process(int arrivalTime, int burstTime, int processNo){
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.processNo = processNo;
    }

    @Override
    public String toString(){
        return String.format("%-6d %-6d     %d", this.processNo, this.arrivalTime, this.burstTime);
    }
    
    public Integer getArrivalTime(){
        return this.arrivalTime;
    }

    public Integer getBurstTime(){
        return this.burstTime;
    }

    public Integer getStartLastBurst(){
        return this.startLastBurst;
    }

    public int getStartExecTime(){
        return this.startExecTime;
    }

    public int getFinishTime(){
        return this.finishTime;
    }

    public int getProcessNo(){
        return this.processNo;
    }

    public void setStartExecTime(int execTime){
        this.startExecTime = execTime;
        if (this.startLastBurst == 0){
            this.startLastBurst = execTime;
        }
    }

    public void setFinishTime(int finishTime){
        this.finishTime = finishTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public void setStartLastBurst(int startLastBurst) {
        this.startLastBurst = startLastBurst;
    }

}
