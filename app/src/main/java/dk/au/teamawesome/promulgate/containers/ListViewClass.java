package dk.au.teamawesome.promulgate.containers;

/**
 * Created by Kenneth on 21-04-2015.
 */
public class ListViewClass{
    private String name;
    private String description;
    private int id;
    private String machineId;
    private String routingServer;
    private boolean isCollapsed;
    private String deviceType;

    public ListViewClass(String name, String description, int id){
        this.name = name;
        this.description = description;
        this.id = id;
        this.isCollapsed = true;
    }

    public void setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    
    public String getMachineId() {
        return machineId;
    }
    
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getRoutingServer() {
        return routingServer;
    }

    public void setRoutingServer(String routingServer) {
        this.routingServer = routingServer;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
