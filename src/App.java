import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
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

        app.sjf();

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
        float avgResp = respTotal / respCount;
        respString += String.format(") / %d = (%s) / %d = %d / %d = %4.2f", respCount, secondRespString, respCount, respTotal, respCount, avgResp);
        System.out.println(respString);

        int waitTotal = 0;
        for(int i : waitTimes){
            waitTotal += i;
        }
        int waitCount = waitTimes.size();
        avgResp = respTotal / respCount;
        waitString += String.format(") / %d = (%s) / %d = %d / %d = %4.2f", waitCount, secondWaitString, waitCount, waitTotal, waitCount, avgResp);
        System.out.println(waitString);
    }
}
