import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roiya on 8/25/2016.
 */
public class Event {


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


    public int getEventTimeStart() {
        return eventTimeStart;
    }

    public int getEventTimeEnd() {
        return eventTimeEnd;
    }

    public double getReqBandwidth() {
        return req_bandwidth;
    }

    private int eventTimeStart;
    private int eventTimeEnd;
    private String eventType;
    private double req_bandwidth;//400 Gbps

    public int getId() {
        return id;
    }

    public Path getEvent_path() {
        return event_path;
    }

    private int id;

    public void setEvent_path(Path event_path) {
        this.event_path = event_path;
    }

    public Path event_path = new Path();

    public int getSource() {
        return source;
    }

    public int getDest() {
        return dest;
    }

    private int source;
    private int dest;

    public Event(int _id, int _eventTimeStart, int _eventTimeEnd, String _event_type, Double _req, int _src, int _dest){
        eventTimeStart = _eventTimeStart;
        eventTimeEnd = _eventTimeEnd;
        req_bandwidth = _req;
        eventType = _event_type;
        source = _src;
        dest = _dest;
        id = _id;

    }

    public String toString(){
        return id + "," + eventTimeStart+", "+eventTimeEnd + ", "+ source + "," + dest +", "+eventType +", " +req_bandwidth;
    }
}
