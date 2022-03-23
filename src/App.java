import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;

public class App {

    public static long seedValue;
    public static int numProcesses;
    public static int lastPossibleArrival;
    public static int maxBurst;
    public static int quantumSize;
    public static int latency;

    public static ArrayList<Process> Processes = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        App app = new App();
        app.getInputs();
        app.createProcesses();

        ArrayList<Process> sorted = sortByArrival(Processes);

        app.randomAlgorithm();

    }

    public void getInputs(){
        Scanner input = new Scanner(System.in);

        System.out.println("Enter a seed value: ");
        seedValue = input.nextLong();
        System.out.println("Enter number of processes (2, 100): ");
        numProcesses = input.nextInt();
        System.out.println("Enter last possible arrival time (0, 99): ");
        lastPossibleArrival = input.nextInt();
        System.out.println("Enter max burst time: ");
        maxBurst = input.nextInt();
        System.out.println("Enter quantum size: ");
        quantumSize = input.nextInt();
        System.out.println("Enter latency: ");
        latency = input.nextInt();
    }

    public void createProcesses(){
        Random random = new Random(seedValue);
        int arrival;
        int burstTime;
        Process process;
        System.out.println(numProcesses + " processes created.");
        System.out.println("P      Arrival    Burst    " );
        for (int i = 0; i < numProcesses; i++){
            arrival = random.nextInt(lastPossibleArrival);
            int range = maxBurst - 1;
            if(maxBurst == 1){
                burstTime = 1;
            }
            else{
                burstTime = 1 + random.nextInt(range);
            }
            process = new Process(arrival, burstTime, i );
            Processes.add(process);
            System.out.println(process);
        }
    }

    public void fcfs(){
        ArrayList<Process> sorted = sortByArrival(Processes);
        int time = 0;
        int contextSwitchCount = 1;
        int procLength; 
        int arrivalTime; 
        Process curProcess;
        ArrayList<Process> completed = new ArrayList<Process>();
        while(sorted.isEmpty() != true){
            curProcess = sorted.get(0);
            arrivalTime = curProcess.getArrivalTime();
            procLength = curProcess.getBurstTime();
            if(time < arrivalTime){
                System.out.println("System waits for process.");
                time = arrivalTime;
            }
            System.out.printf("@t=%d, P%d selected for %d units \n" , time, curProcess.getProcessNo(), procLength);
            curProcess.setStartExecTime(time);
            time += procLength;
            curProcess.setFinishTime(time);
            completed.add(curProcess);
            sorted.remove(curProcess);
            if(sorted.isEmpty()){
                System.out.printf("Completed in %d seconds\n", time );
            }
            else{
                System.out.printf("@t=%d, context switch %d occurs \n", time, contextSwitchCount);
            }
            contextSwitchCount +=1;
            time += latency;
        }

        
    }

    public void sjf(){
        ArrayList<Process> sorted = sortByArrival(Processes);
        int time = 0;
        int contextSwitchCount = 1;
        int procLength; 
        int arrivalTime; 
        Process curProcess;
        Process nextProcess;
        boolean found = false;
        ArrayList<Process> completed = new ArrayList<Process>();
        while(sorted.isEmpty() != true){
            found = false;
            nextProcess = sorted.get(0);
            curProcess = sorted.get(0);
            for(Process i : sorted){
                if(i.getArrivalTime() <= time){
                    curProcess = i;
                    found = true;
                    break;
                }
                if(i.getArrivalTime() < nextProcess.getArrivalTime()){
                    nextProcess = i;
                }
            }
            if(!found){
                curProcess = nextProcess;
                System.out.println("System waits for a process");
                time = curProcess.getArrivalTime();
            }

            arrivalTime = curProcess.getArrivalTime();
            procLength = curProcess.getBurstTime();
            
            System.out.printf("@t=%d, P%d selected for %d units \n" , time, curProcess.getProcessNo(), procLength);
            curProcess.setStartExecTime(time);
            time += procLength;
            curProcess.setFinishTime(time);
            completed.add(curProcess);
            sorted.remove(curProcess);
            if(sorted.isEmpty()){
                System.out.printf("Completed in %d seconds\n", time );
            }
            else{
                System.out.printf("@t=%d, context switch %d occurs \n", time, contextSwitchCount);
            }
            contextSwitchCount +=1;
            time += latency;
        }
        calculate(completed);
    }

    public void randomAlgorithm () {
        Random rand = new Random();
        ArrayList <Process> randomList = new ArrayList<>();
        ArrayList <Process> copyProcessList = new ArrayList<>();

        int time = 0;
        int contextSwitchCount = 1;
        int procLength; 
        int arrivalTime; 
        Process curProcess;
        Process nextProcess;
        boolean found = false;
        ArrayList<Process> completed = new ArrayList<Process>();
        ArrayList<Process> WaitingQueue = new ArrayList<Process>();


        for (Process p: Processes) {
            copyProcessList.add(p);
        }

        System.out.println();

        while (contextSwitchCount <= 5) {
            for (int i = 0; i < copyProcessList.size(); i++) {
                if (copyProcessList.get(i).getArrivalTime() <= time) {
                    WaitingQueue.add(copyProcessList.get(i));
                    copyProcessList.remove(copyProcessList.get(i));
                }
            }

            if (WaitingQueue.size() > 0) {
                if (WaitingQueue.size() == 1) {
                    curProcess = WaitingQueue.remove(0);
                    curProcess.setStartExecTime(time);
                    curProcess.setFinishTime(time + curProcess.getBurstTime());
                    //copyProcessList.remove(0);
                }
                
                else {
                    int processIndex = rand.nextInt(WaitingQueue.size());
                    curProcess = WaitingQueue.remove(processIndex);
                    curProcess.setStartExecTime(time);
                    curProcess.setFinishTime(time + curProcess.getBurstTime());


                }
                
                randomList.add(curProcess);

                arrivalTime = curProcess.getArrivalTime();
                procLength = curProcess.getBurstTime();

                System.out.printf("@t=%d, P%d selected for %d units \n" , time, curProcess.getProcessNo(), procLength);
                curProcess.setStartExecTime(time);
                time += procLength;

                if(copyProcessList.isEmpty()){
                    System.out.printf("Completed in %d seconds\n", time );
                }
                else{
                    System.out.printf("@t=%d, context switch %d occurs \n", time, contextSwitchCount);
                }
                
            contextSwitchCount +=1;
            }
            else {
                String waiting = "Waiting";
                System.out.print(waiting);
            }
            time++;
        }

        calculate(randomList);
    }


    public void RR () {
        ArrayList <Process> copyProcessList = new ArrayList<>();
        ArrayList <Integer> remainingBurstTimes = new ArrayList<>();
        ArrayList <Process> finalProcesses = new ArrayList<>();
        ArrayList <Process> waitingTimes = new ArrayList<>();
        Queue<Process> WaitingQueue = new LinkedList<>();
        Process curProcess;


        for (Process p : Processes) {
            copyProcessList.add(p);
        }

        int time = 0;
        int contextSwitchCount = 1;
        int procLength; 

        while (true) {
            boolean done = false;
            for (int i = 0; i < copyProcessList.size(); i++) {
                if (copyProcessList.get(i).getArrivalTime() <= time) {
                    WaitingQueue.add(copyProcessList.get(i));
                    copyProcessList.remove(i);
                }
            }
            time ++;

            while (!WaitingQueue.isEmpty()) {
                
                curProcess = WaitingQueue.remove();

                if (curProcess.getBurstTime() > quantumSize) {
                    curProcess.setBurstTime(curProcess.getBurstTime() - quantumSize);
                    WaitingQueue.add(curProcess);
                    System.out.printf("@t=%d, P%d selected for %d units \n" , time, curProcess.getProcessNo(), quantumSize);
                    System.out.printf("@t=%d, context switch %d occurs \n", time, contextSwitchCount);

                    curProcess.setStartExecTime(time);
                    time += quantumSize;
                    contextSwitchCount++;
                }

                else
                {
                    int bt = curProcess.getBurstTime();
                    System.out.printf("@t=%d, P%d selected for %d units \n" , time, curProcess.getProcessNo(), bt);
                    
                    if (WaitingQueue.size() >= 1) {
                        System.out.printf("@t=%d, context switch %d occurs \n", time, contextSwitchCount);
                    }

                    curProcess.setFinishTime(time);
                    finalProcesses.add(curProcess);
                    curProcess.setBurstTime(0);
                    time += bt;
                    contextSwitchCount++;
                }
                for (int i = 0; i < copyProcessList.size(); i++) {
                    if (copyProcessList.get(i).getArrivalTime() <= time) {
                        WaitingQueue.add(copyProcessList.get(i));
                        copyProcessList.remove(copyProcessList.get(i));
                    }
                }
            }

            if (copyProcessList.isEmpty()) {
                break;
            }

        }
        for (Process p : finalProcesses) {
            p.setStartLastBurst(p.getFinishTime());
            System.out.println(p);
        }
        calculate(finalProcesses);
    }

    public static ArrayList<Process> sortByArrival(ArrayList<Process> list){
        ArrayList<Process> bufferList = new ArrayList<Process>(list.size());
        for (Process i : list){
            bufferList.add(i);
        }
        bufferList.sort((o1, o2) 
        -> o1.getArrivalTime().compareTo(o2.getArrivalTime())

        );

        return bufferList;
    }

    public static ArrayList<Process> sortByLength(ArrayList<Process> list){
        ArrayList<Process> bufferList = new ArrayList<Process>(list.size());
        for (Process i : list){
            bufferList.add(i);
        }
        bufferList.sort((o1, o2) 
        -> o1.getBurstTime().compareTo(o2.getBurstTime())

        );

        return bufferList;
    }

    public static void calculate(ArrayList<Process> list)  {
        ArrayList<Integer> waitTimes = new ArrayList<Integer>();
        ArrayList<Integer> respTimes = new ArrayList<Integer>();
        int waittime;
        int resptime; 
        String waitString = "((";
        String secondWaitString = "(";
        String respString = "((";
        String secondRespString = "(";
        boolean firstTime = true;
        
        for(Process i : list){
            waittime = i.getStartLastBurst() - i.getArrivalTime();
            waitTimes.add(waittime);
            resptime = i.getStartExecTime() - i.getArrivalTime();
            respTimes.add(resptime);
            if(firstTime){
                respString += String.format("%d - %d)",i.getStartExecTime(), i.getArrivalTime());
                secondRespString += resptime;
                waitString += String.format("%d - %d)",i.getStartLastBurst(), i.getArrivalTime());
                secondWaitString += waittime;
                firstTime = false;
            }
            else{
                respString += String.format("+(%d - %d)",i.getStartExecTime(), i.getArrivalTime());
                secondRespString += ("+" + resptime);
                waitString += String.format("+(%d - %d)",i.getStartLastBurst(), i.getArrivalTime());
                secondWaitString += ("+" + waittime);
            }
        }
        int respTotal = 0;
        for(int i : respTimes){
            respTotal += i;
        }
        int respCount = respTimes.size();
        float avgResp = (float)respTotal / (float)respCount;
        respString += String.format(") / %d = (%s) / %d = %d / %d = %4.2f", respCount, secondRespString, respCount, respTotal, respCount, avgResp);
        System.out.println(respString);

        int waitTotal = 0;
        for(int i : waitTimes){
            waitTotal += i;
        }
        int waitCount = waitTimes.size();
        avgResp = (float)respTotal / (float)respCount;
        waitString += String.format(") / %d = (%s) / %d = %d / %d = %4.2f", waitCount, secondWaitString, waitCount, waitTotal, waitCount, avgResp);
        System.out.println(waitString);
    }
}
