import com.sun.corba.se.impl.orbutil.closure.Constant;

import java.io.IOException;
import java.util.*;
import java.io.IOException;

import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;
import jsc.distributions.*;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;


/**
 * Created by Roiya on 8/25/2016.
 */
public class TrafficEvents {
    public static List<Event> eventList = new ArrayList<Event>();
    public static Topology testTopo;
    public static int count =0;
    public static double load_offered = 0.0;

    public static int blocked_40 = 0;
    public static int blocked_100 = 0;
    public static int blocked_200 = 0;
    public static int blocked_400 = 0;
    public static double total_gbps_requested = 0.0;
   // public static int total_ghzs_requested = 0.0;

    public static double bw_actual_usage_40 = 0;
    public static double bw_actual_usage_100 = 0;
    public static double bw_actual_usage_200 = 0;
    public static double bw_actual_usage_400 = 0;

    public static double spectrum_served = 0.0;
    public static double spectrum_blocked = 0.0;


    public static double signal_use_40 = 0;
    public static double signal_use_100 = 0;
    public static double signal_use_200 = 0;
    public static double signal_use_400 = 0;

    public static void simulate() {

        //System.out.println(testTopo.isFlex(12));


        while (true) {
            if (eventList.isEmpty())
                break;
            Event ev = eventList.remove(0);
            if (ev.getEventType() == "start")
                handleStart(ev);
            if (ev.getEventType() == "end")
                handleEnd(ev);
        }
    }


    //############### SEDRA ####
    public static void handleStart(Event ev) {

        total_gbps_requested += ev.getReqBandwidth();
        ArrayList<Path> candidates = new ArrayList<Path>();
        Path best_path = new Path();
        double lowest_bw = 10000000000.0;
       for (int i = 0; i < (int)testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(0); i++) {
            Path p = (Path) testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(i+1);
           // System.out.println(p);
            if (check_bandwidth_availablility(p, ev.getReqBandwidth()) == true)
                candidates.add(p);

        }

        if(candidates.isEmpty()){
            if(ev.getReqBandwidth()==40.0)
                blocked_40++;
            else if(ev.getReqBandwidth()==100.0)
                blocked_100++;
            else if(ev.getReqBandwidth()==200.0)
                blocked_200++;
            else if(ev.getReqBandwidth()==400.0)
                blocked_400++;
            //System.out.println("no path");
            //Delete end event
            for(int i=0;i<eventList.size();i++){
                Event event = eventList.get(i);
                if(event.getEventType()=="end" && event.getId()==ev.getId()){
                    eventList.remove(i);
                }
            }

            return;
        }
       // System.out.println(" path");
        //Calculate the best candidate by picking the path with lowest spectrum waste
        for(int i=0; i< candidates.size();i++){
            double bw = get_actual_used_bw(candidates.get(i), ev.getReqBandwidth());
            if(bw<lowest_bw){
                best_path = candidates.get(i);
                lowest_bw = bw;
            }
        }
        //Subtruct the available capacity along the path
        subtruct_bandwidth_availablility(best_path,ev.getReqBandwidth());

        //System.out.println(best_path.toString() + " short " + ((Path) testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(1)).toString());
        //double signal_usage =  signal_bandwidth(best_path,ev.getReqBandwidth());


/*
        if(ev.getReqBandwidth()==40.0)
            bw_actual_usage_40 += lowest_bw;
        if(ev.getReqBandwidth()==100.0)
            bw_actual_usage_100 += lowest_bw;
        if(ev.getReqBandwidth()==200.0)
            bw_actual_usage_200 += lowest_bw;
        if(ev.getReqBandwidth()==400.0)
            bw_actual_usage_400 += lowest_bw;

        if(ev.getReqBandwidth()==40.0)
            signal_use_40 += signal_usage;
        if(ev.getReqBandwidth()==100.0)
            signal_use_100 += signal_usage;
        if(ev.getReqBandwidth()==200.0)
            signal_use_200 += signal_usage;
        if(ev.getReqBandwidth()==400.0)
            signal_use_400 += signal_usage;*/

        //Add the path inside event

        //print_path(best_path);

        //Update end event
        for(int i=0;i<eventList.size();i++){
            Event event = eventList.get(i);
            if(event.getEventType()=="end" && event.getId()==ev.getId()){
                event.setEvent_path(best_path);
                eventList.set(i,event);
                //      print_path(eventList.get(i).event_path);
            }
        }

    }


    public static void handleEnd(Event ev) {
        //Lease the link capacity along the path: event needs to remember path
        //System.out.println(ev.toString());
        //print_path(ev.event_path);
        release_bandwidth_availablility(ev.event_path,ev.getReqBandwidth());


    }

/*
    ###############################################################
    Shortest path simulation

     */
public static void simulate_short() {

    while (true) {
        if (eventList.isEmpty())
            break;
        Event ev = eventList.remove(0);
        if (ev.getEventType() == "start")
            handleStart_Short(ev);
        if (ev.getEventType() == "end")
            handleEnd_Short(ev);
    }
}
public static void handleStart_Short(Event ev) {
    total_gbps_requested += ev.getReqBandwidth();
    Path short_path = new Path();

    boolean no_path = false;
    if((int)testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(0)>0)
        short_path = (Path) testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(1);
    else
        no_path = true;

    if (no_path==true) {
        if(ev.getReqBandwidth()==40.0)
            blocked_40++;
        if(ev.getReqBandwidth()==100.0)
            blocked_100++;
        if(ev.getReqBandwidth()==200.0)
            blocked_200++;
        if(ev.getReqBandwidth()==400.0)
            blocked_400++;
        //System.out.println("no path");
        //Delete end event
        for(int i=0;i<eventList.size();i++){
            Event event = eventList.get(i);
            if(event.getEventType()=="end" && event.getId()==ev.getId()){
                eventList.remove(i);
            }
        }
        return;
    }
        //System.out.println(p);
    if (check_bandwidth_availablility(short_path, ev.getReqBandwidth()) == false) {
        if(ev.getReqBandwidth()==40.0)
            blocked_40++;
        if(ev.getReqBandwidth()==100.0)
            blocked_100++;
        if(ev.getReqBandwidth()==200.0)
            blocked_200++;
        if(ev.getReqBandwidth()==400.0)
            blocked_400++;
       // System.out.println("no path");
        //Delete end event
        for(int i=0;i<eventList.size();i++){
            Event event = eventList.get(i);
            if(event.getEventType()=="end" && event.getId()==ev.getId()){
                eventList.remove(i);
            }
        }
        return;
    }

 //   double lowest_bw = get_actual_used_bw(short_path, ev.getReqBandwidth());

    //Allocate the available capacity along the path
    subtruct_bandwidth_availablility(short_path,ev.getReqBandwidth());

  //  double signal_usage =  signal_bandwidth(short_path,ev.getReqBandwidth());

/*

    if(ev.getReqBandwidth()==40.0)
        bw_actual_usage_40 += lowest_bw;
    if(ev.getReqBandwidth()==100.0)
        bw_actual_usage_100 += lowest_bw;
    if(ev.getReqBandwidth()==200.0)
        bw_actual_usage_200 += lowest_bw;
    if(ev.getReqBandwidth()==400.0)
        bw_actual_usage_400 += lowest_bw;

   if(ev.getReqBandwidth()==40.0)
        signal_use_40 += signal_usage;
    if(ev.getReqBandwidth()==100.0)
        signal_use_100 += signal_usage;
    if(ev.getReqBandwidth()==200.0)
        signal_use_200 += signal_usage;
    if(ev.getReqBandwidth()==400.0)
        signal_use_400 += signal_usage;*/

    //Add the path inside event

    //print_path(best_path);

    //Update end event
    for(int i=0;i<eventList.size();i++){
        Event event = eventList.get(i);
        if(event.getEventType()=="end" && event.getId()==ev.getId()){
            event.setEvent_path(short_path);
            eventList.set(i,event);
          //  print_path(eventList.get(i).event_path);
        }
    }

}


    public static void handleEnd_Short(Event ev) {
        //Lease the link capacity along the path: event needs to remember path
        //System.out.println(ev.toString());
        //print_path(ev.event_path);
        release_bandwidth_availablility(ev.event_path,ev.getReqBandwidth());
    }



/*
#########################
 */

    public static void main(String args[]) {
      //  ArrayList<Double> total__actual_usage = new ArrayList<Double>();
       // ArrayList<Double> total_signal_usage = new ArrayList<Double>();
        ArrayList<Double> bandwidth_block_ratio = new ArrayList<Double>();

        double util = 0.0;



//        for(int i=0; i<Constants.num_runs;i++) {
            //initialize
            createEventList();
            System.out.println(Constants.TOTAL_CAPACITY + " " + load_offered + " " + load_offered/Constants.TOTAL_CAPACITY);


//////        getCost();
////          for(int i=0; i<100; i++)
////              System.out.println(eventList.get(i).toString());
//            try {
//                //topo.txt, update classes accordingly
//                testTopo = new Topology("topo.txt");
//                //handle start
//                //handle end event
//            } catch (IOException e) {
//                return;
//            }
//            simulate();
////            for (int k = 0; k < 14; k++)
////                for (int j = 0; j < 14; j++) {
////                    if(testTopo.adjMatrix[i][j].get(0)=="1") {
////                        Link l = (Link) testTopo.adjMatrix[i][j].get(1);
////                        util += (double) (l.getCapacity() - l.avail_capacity) / l.getCapacity();
////                        System.out.println(util);
////                    }
////                }
//
//        //    total__actual_usage.add(bw_actual_usage_40 + bw_actual_usage_100 + bw_actual_usage_200 + bw_actual_usage_400);
//        //    total_signal_usage.add(signal_use_40 + signal_use_100 + signal_use_400 + signal_use_200);
//            bandwidth_block_ratio.add((blocked_40*40.0 + blocked_100*100.0 + blocked_200*200.0 + blocked_400*400.0)/total_gbps_requested);
//
//            blocked_40 = 0;
//            blocked_100 = 0;
//            blocked_200 = 0;
//            blocked_400 = 0;
//            total_gbps_requested = 0.0;
//
//            bw_actual_usage_40 = 0;
//            bw_actual_usage_100 = 0;
//            bw_actual_usage_200 = 0;
//            bw_actual_usage_400 = 0;
//
//            signal_use_40 = 0;
//            signal_use_100 = 0;
//            signal_use_200 = 0;
//            signal_use_400 = 0;
//
//            eventList = new ArrayList<Event>();
//
//
//        }
//
//      //  double total_actual_usage = avg_cal(total__actual_usage);
//        //double avg_total_signal_usage = avg_cal(total_signal_usage);
//        double bb_ratio = avg_cal(bandwidth_block_ratio);
//
//        System.out.println("\nbandwidth blocking ratio: " + bb_ratio);
//        //System.out.println(avg_total_waste);
//      //  System.out.println(avg_total_signal_usage);
//     //   System.out.println("spectral utilization: " + avg_total_signal_usage/total_actual_usage); //(Constants.num_links*8000)); //
//
//    //    System.out.println("spectrum efficiency: " + avg_total_signal_usage/avg_total_waste);
//        //System.out.println(total_blocked/);

    }

    // Create events of HOURS and add to list
    public static void createEventList() {
        Random randomSelector = new Random(System.currentTimeMillis());
        int global_time = 0;
        for (int i = 0; i < Constants.num_req; i++) {

            Uniform uniform_s = new Uniform (0, 800);
            int start = (int)uniform_s.random();
            //start = next poisson arrival
            //PoissonDistribution poisson;
            //poisson = new PoissonDistribution(20000);
            //int start = poisson.sample();
           // global_time += 10;
           // System.out.println(start);
            // end = start + next exponential duration
            ExponentialDistribution expo;
            expo = new ExponentialDistribution(2);
            double exp = expo.sample();
            double end;
            end = exp + start;
           // System.out.println("e " + exp);
            if((start==(int)end))
                end = end + 5.0;

            //src and dest = uniform node num
            Uniform uniform = new Uniform (0, 14);
            int src = (int)uniform.random();
            int dst = (int)uniform.random();

            //System.out.println(src);
            // req_speed = uniformly taken from different combination
            int indx = randomSelector.nextInt() % Constants.speed_types.length;
            if (indx < 0)
                indx = -1 * indx;
            Double req_speed = Constants.speed_types[indx];
            load_offered += req_speed;
            //System.out.println(req_speed);
            int id = count;
            count++;

            if(src!=dst) {
                Event ev = new Event(id, global_time + start, global_time + (int) end, "start", req_speed, src, dst);
                insertEventInSortedList(ev, global_time + start);
                ev = new Event(id, global_time + start, global_time + (int) end, "end", req_speed, src, dst);
                insertEventInSortedList(ev, global_time + (int) end);
            }
        }
    }

    public static void insertEventInSortedList(Event event, int _time) {
        if (eventList.isEmpty()) {
            eventList.add(event);
            //System.out.println("empty");
            return;
        }
        Event evnt;
        int pos = 0;
        int list_event_time = 0;
        for (pos = 0; pos < eventList.size(); pos++) {
            evnt = eventList.get(pos);
            if (evnt.getEventType() == "start")
                list_event_time = evnt.getEventTimeStart();
            else
                list_event_time = evnt.getEventTimeEnd();
            if (list_event_time >= _time)
                break;
        }
        if (pos >= eventList.size()) {
            eventList.add(event);
        } else {
            eventList.add(pos, event);
        }
    }

    public static boolean check_bandwidth_availablility(Path p, double req){
        Vector<Link> links = p.getLinks();

        for(int i=0; i<links.size();i++){
            int src = links.get(i).startNodeID;
            int dst = links.get(i).endNodeID;
            Link l = (Link)testTopo.adjMatrix[src][dst].get(1);
            Double ghz = mixed_grid(links.get(0).startNodeID, links.get(i).endNodeID, links.get(i).startNodeID, req);
            if(l.avail_capacity>=ghz)
                continue;
            else
                return false;
        }
        return true;
    }

    public static double getWaste(Path p, double req){
        Vector<Link> links = p.getLinks();
        Double total_waste = 0.0;
        for(int i=0; i<links.size();i++){
            int src = links.get(i).startNodeID;
            int dst = links.get(i).endNodeID;
            Link l = (Link)testTopo.adjMatrix[src][dst].get(1);

            Double ghz_waste = gbpsToGhzsWaste(l.getTyp(), req);
            total_waste = total_waste + ghz_waste;
        }
        return total_waste;
    }

    public static double get_actual_used_bw(Path p, double req){
        Vector<Link> links = p.getLinks();
        Double total_util = 0.0;
        for(int i=0; i<links.size();i++){
            int src = links.get(i).startNodeID;
            int dst = links.get(i).endNodeID;
            Link l = (Link)testTopo.adjMatrix[src][dst].get(1);

            Double ghz_util = mixed_grid(links.get(0).startNodeID, links.get(i).endNodeID, links.get(i).startNodeID, req);
            total_util = total_util + ghz_util;
        }
        return total_util;
    }

    public static double mixed_grid(int path_start, int link_end, int link_start, double req){
        //is the link flex

        //
        //Start node Fixed-grid
        if(testTopo.isFlex(path_start)==false){
            //link start fixed -> *
            if(testTopo.isFlex(link_start)==false)
                return gbpsToGhzs_actual_used(0, req);
            // flex -> fix
            else if (testTopo.isFlex(link_start)==true && testTopo.isFlex(link_end)==false)
                return gbpsToGhzs_actual_used(0, req);
                // flex -> flex
            else if (testTopo.isFlex(link_start)==true && testTopo.isFlex(link_end)==true)
                return gbpsToGhzs_actual_used(1, req);
        }
        //Start node flex-grid
        else{
            //link start flex -> *
            if(testTopo.isFlex(link_start)==true)
                return gbpsToGhzs_actual_used(1, req);
                // fix -> flex - may change later
            else if (testTopo.isFlex(link_start)==false && testTopo.isFlex(link_end)==true)
                return gbpsToGhzs_actual_used(0, req);
                // fix -> fix
            else if (testTopo.isFlex(link_start)==false && testTopo.isFlex(link_end)==false)
                return gbpsToGhzs_actual_used(0, req);
        }

        return 0.0;
    }

    public static double gbpsToGhzsWaste(int typ, double req){
        if(req == 40.0 && typ==0)
            return 30.0 + 1 * Constants.fix_guardband;
        else if(req == 40.0 && typ==1)
            return 0.0 + 1 * Constants.flex_guardband;
        else if(req == 100.0 && typ==0)
            return 0.0 + 1 * Constants.fix_guardband;
        else if(req == 100.0 && typ==1)
            return 0.0 + 1 * Constants.flex_guardband;
        else if(req == 200.0 && typ==0)
            return 0.0 + 2 * Constants.fix_guardband;
        else if(req == 200.0 && typ==1)
            return 0.0 + 1 * Constants.flex_guardband;
        else if(req == 400.0 && typ==0)
            return 0.0 + 4 * Constants.fix_guardband;
        else if(req == 400.0 && typ==1)
            return 0.0 + 1 * Constants.flex_guardband;
        return 0.0;
    }

    public static void subtruct_bandwidth_availablility(Path p, double req){
        Vector<Link> links = p.getLinks();
        for(int i=0; i<links.size();i++){
            int src = links.get(i).startNodeID;
            int dst = links.get(i).endNodeID;
            Link l = (Link)testTopo.adjMatrix[src][dst].get(1);
            Double ghz = mixed_grid(links.get(0).startNodeID, links.get(i).endNodeID, links.get(i).startNodeID, req);
            ((Link)testTopo.adjMatrix[src][dst].get(1)).avail_capacity = l.avail_capacity - ghz;
        }
    }

    public static void release_bandwidth_availablility(Path p, double req){
        Vector<Link> links = p.getLinks();
        for(int i=0; i<links.size();i++){
            int src = links.get(i).startNodeID;
            int dst = links.get(i).endNodeID;
            Link l = (Link)testTopo.adjMatrix[src][dst].get(1);
            Double ghz = mixed_grid(links.get(0).startNodeID, links.get(i).endNodeID, links.get(i).startNodeID, req);
            ((Link)testTopo.adjMatrix[src][dst].get(1)).avail_capacity = l.avail_capacity + ghz;
        }
    }

    public static double signal_bandwidth(Path p, double req){
        Vector<Link> links = p.getLinks();
        double signal_usage = 0.0;
        for(int i=0; i<links.size();i++){
            int src = links.get(i).startNodeID;
            int dst = links.get(i).endNodeID;
            Link l = (Link)testTopo.adjMatrix[src][dst].get(1);
            signal_usage += gbpsToGhzsSignal(l.getTyp(),req);
        }
        return signal_usage;
    }
    //typ 0 is Fixed, typ 1 is  flex
    public static double gbpsToGhzs_actual_used(int typ, double req){
        if(req == 40.0 && typ==0)
            return 50.0 + 1 * Constants.fix_guardband;
        else if(req == 40.0 && typ==1)
            return 25.0 + 1 * Constants.flex_guardband;
        else if(req == 100.0 && typ==0)
            return 50.0 + 1 * Constants.fix_guardband;
        else if(req == 100.0 && typ==1)
            return 37.5 + 1 * Constants.flex_guardband;
        else if(req == 200.0 && typ==0)
            return 100.0 + 2 * Constants.fix_guardband;
        else if(req == 200.0 && typ==1)
            return 75.0 + 1 * Constants.flex_guardband;
        else if(req == 400.0 && typ==0)
            return 200.0 + 4 * Constants.fix_guardband;
        else if(req == 400.0 && typ==1)
            return 125.0 + 1 * Constants.flex_guardband;
        return 0.0;
    }

    public static double gbpsToGhzsSignal(int typ, double req){
        if(req == 40.0 && typ==0)
            return 20.0;
        else if(req == 40.0 && typ==1)
            return 25.0;
        else if(req == 100.0 && typ==0)
            return 50.0;
        else if(req == 100.0 && typ==1)
            return 37.5;
        else if(req == 200.0 && typ==0)
            return 100.0;
        else if(req == 200.0 && typ==1)
            return 75.0;
        else if(req == 400.0 && typ==0)
            return 200.0;
        else if(req == 400.0 && typ==1)
            return 125.0;
        return 0.0;
    }

    public static void print_path(Path p){
        Vector<Link> links = p.getLinks();
        for(int i=0; i<links.size();i++){
            int src = links.get(i).startNodeID;
            int dst = links.get(i).endNodeID;
            Link l = (Link)testTopo.adjMatrix[src][dst].get(1);
            System.out.println(l.getStartNodeID()+ ", " + l.getEndNodeID() + ", " + l.avail_capacity );
        }
    }

    public static double avg_cal(ArrayList<Double> list){
        double sum = 0.0;
        for(int i=0;i<list.size();i++){
            sum += list.get(i);
        }
        return sum/list.size();
    }

    public static double err_up_cal(ArrayList<Double> list, double avg){
        double err = 0.0;
        int count = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i)>=avg) {
                err += list.get(i) - avg;
                count++;
            }
        }
        return err/count;
    }

    public static double err_down_cal(ArrayList<Double> list, double avg){
        double err = 0.0;
        int count = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i)<avg) {
                err += avg-list.get(i);
                count++;
            }
        }
        return err/count;
    }


    /*
#####################################################################
 */
    public static void simulate_fit() {

        while (true) {
            if (eventList.isEmpty())
                break;
            Event ev = eventList.remove(0);
            if (ev.getEventType() == "start")
                handleStart_fit(ev);
            if (ev.getEventType() == "end")
                handleEnd_fit(ev);
        }
    }

    public static void handleStart_fit(Event ev) {
        //Find candidate paths: paths which start from i to j and has available capacity
        // System.out.println(ev.toString());
        // ArrayList<Path> candidates = new ArrayList<Path>();
        Path best_path = null;
        //  double lowest_waste = 10000000000.0;
        // int failed_path = 0;
        // if(testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(0)!= new Integer(0)) {
        //System.out.println(ev.getSource() + "," + ev.getDest() + "," +  testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(0));
        for (int i = 0; i < (int)testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(0); i++) {
            Path p = (Path) testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(i+1);
            //  System.out.println(p);
            if (check_bandwidth_availablility(p, ev.getReqBandwidth()) == true) {
                //candidates.add(p);
                best_path = p;
            }

        }
        //Chack for blocking
        // if(failed_path==(int)testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(0))

        if(best_path == null){
            if(ev.getReqBandwidth()==40.0)
                blocked_40++;
            if(ev.getReqBandwidth()==100.0)
                blocked_100++;
            if(ev.getReqBandwidth()==200.0)
                blocked_200++;
            if(ev.getReqBandwidth()==400.0)
                blocked_400++;
            //     System.out.println("no path");
            //Delete end event
            for(int i=0;i<eventList.size();i++){
                Event event = eventList.get(i);
                if(event.getEventType()=="end" && event.getId()==ev.getId()){
                    eventList.remove(i);
                }
            }
            return;
        }

        //Calculate the best candidate by picking the path with lowest spectrum waste
        //for(int i=0; i< candidates.size();i++){
        double lowest_bw = get_actual_used_bw(best_path, ev.getReqBandwidth());

        //Subtruct the available capacity along the path
        subtruct_bandwidth_availablility(best_path,ev.getReqBandwidth());

        // System.out.println(best_path.toString() + " short " + ((Path) testTopo.pathMatrix[ev.getSource()][ev.getDest()].get(1)).toString());
        double signal_usage =  signal_bandwidth(best_path,ev.getReqBandwidth());


        if(ev.getReqBandwidth()==40.0)
            bw_actual_usage_40 += lowest_bw;
        if(ev.getReqBandwidth()==100.0)
            bw_actual_usage_100 += lowest_bw;
        if(ev.getReqBandwidth()==200.0)
            bw_actual_usage_200 += lowest_bw;
        if(ev.getReqBandwidth()==400.0)
            bw_actual_usage_400 += lowest_bw;

        if(ev.getReqBandwidth()==40.0)
            signal_use_40 += signal_usage;
        if(ev.getReqBandwidth()==100.0)
            signal_use_100 += signal_usage;
        if(ev.getReqBandwidth()==200.0)
            signal_use_200 += signal_usage;
        if(ev.getReqBandwidth()==400.0)
            signal_use_400 += signal_usage;

        //Add the path inside event

        //print_path(best_path);

        //Update end event
        for(int i=0;i<eventList.size();i++){
            Event event = eventList.get(i);
            if(event.getEventType()=="end" && event.getId()==ev.getId()){
                event.setEvent_path(best_path);
                eventList.set(i,event);
                //      print_path(eventList.get(i).event_path);
            }
        }

    }


    public static void handleEnd_fit(Event ev) {
        //Lease the link capacity along the path: event needs to remember path
        //System.out.println(ev.toString());
        //print_path(ev.event_path);
        release_bandwidth_availablility(ev.event_path,ev.getReqBandwidth());


    }

}
