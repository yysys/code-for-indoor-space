package dk.aau.cs.idq.secondTest;

import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.indoorentities.Point;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;
import dk.aau.cs.idq.utilities.RoomType;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class mapJson {

    public List<JSONObject> getPoints(Par pars) {
        List<JSONObject> ans = new LinkedList<JSONObject>();

        double x1 = pars.getX1();
        double x2 = pars.getX2();
        double y1 = pars.getY1();
        double y2 = pars.getY2();

        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        JSONObject json3 = new JSONObject();
        JSONObject json4 = new JSONObject();

        json1.put("x", x1);
        json1.put("y", y1);
        ans.add(json1);

        json2.put("x", x2);
        json2.put("y", y1);
        ans.add(json2);

        json3.put("x", x2);
        json3.put("y", y2);
        ans.add(json3);

        json4.put("x", x1);
        json4.put("y", y2);
        ans.add(json4);

        return ans;
    }

    public List<JSONObject> getRegions(int nowFloor) {

        List<JSONObject> ans = new LinkedList<JSONObject>();

        List<Par> pars = ReadPar.getPar();

        for (int i = 0; i < pars.size(); i++) {
            if (pars.get(i).getmFloor() == nowFloor) {
                JSONObject json = new JSONObject();
                json.put("id", pars.get(i).getmID());
                json.put("nodeId", pars.get(i).getmID());
                if (pars.get(i).getmType() ==  RoomType.ROOM) {
                    json.put("color", "店铺");
                }
                else if (pars.get(i).getmType() == RoomType.HALLWAY) {
                    json.put("color", "走廊");
                }
                else {
                    json.put("color", "楼道");
                }

                json.put("points", getPoints(pars.get(i)));

                ans.add(json);
            }
        }

        return ans;
    }

    public JSONObject getColors() {
        JSONObject json = new JSONObject();

        json.put("电梯", "#9bcdff");
        json.put("外墙", "#727272");
        json.put("门", "#505665");
        json.put("中庭", "#9493ca");
        json.put("楼道", "#d1ed83");
        json.put("走廊", "#ffffff");
        json.put("设施间", "#b0cfd3");
        json.put("内墙", "#b2b774");
        json.put("店铺", "#f4f4f4");

        return json;
    }

    public JSONObject getConfig() {
        JSONObject json = new JSONObject();

        json.put("colors", getColors());

        return json;
    }

    double getDist(Point point, double x, double y) {
        double xx = point.getX() - x;
        double yy = point.getY() - y;

        return Math.sqrt(xx * xx + yy * yy);
    }

    public JSONObject getLine(Door door) {

        double ratio = 0.2;

        JSONObject json = new JSONObject();

        double x = door.getX();
        double y = door.getY();

        if (door.getmPartitions().size() > 2) {
            json.put("x1", x);
            json.put("x2", x);
            json.put("y1", y);
            json.put("y2", y);
        }
        else {

            ArrayList<Par> pars = ReadPar.getPar();

            List<Point> points = new LinkedList<>();

            for (Integer parID : door.getmPartitions()) {
                Par par = pars.get(parID);

                points.add(new Point(par.getX1(), par.getY1()));
                points.add(new Point(par.getX1(), par.getY2()));
                points.add(new Point(par.getX2(), par.getY1()));
                points.add(new Point(par.getX2(), par.getY2()));
            }

            double ans1 = 100000;
            double ans2 = 100000;

            double xx1 = 0, xx2 = 0, yy1 = 0, yy2 = 0;

            for (int i = 0; i < points.size(); i++) {
                double dist = getDist(points.get(i), x, y);

                if (dist < ans1) {
                    xx1 = points.get(i).getX();
                    yy1 = points.get(i).getY();

                    yy2 = yy1;
                    xx2 = xx1;
                    ans1 = dist;
                }
                else if (dist < ans2) {
                    xx2 = points.get(i).getX();
                    yy2 = points.get(i).getY();

                    ans2 = dist;
                }
            }

            double vecx = x - xx1;
            double vecy = y - yy1;

            json.put("x1", x + vecx * ratio);
            json.put("y1", y + vecy * ratio);

            json.put("x2", x - vecx * ratio);
            json.put("y2", y - vecy * ratio);
        }

        return json;
    }

    public List<JSONObject> getDoors(int nowFloor) {
        List<JSONObject> ans = new LinkedList<JSONObject>();

        List<Door> doors = ReadDoor.getDoor();

        for (int i = 0; i < doors.size(); i++) {
            if (doors.get(i).getmFloor() == nowFloor) {
                JSONObject json = new JSONObject();
                json.put("id", doors.get(i).getmID());
                json.put("color", "门");
                json.put("doorType", "NORMAL");
                json.put("width", "NORMAL");
                json.put("line", getLine(doors.get(i)));

                ans.add(json);
            }
        }

        return ans;
    }

    public JSONObject getLabelConfig(Par par) {

        JSONObject json = new JSONObject();

        if (par.getmType() == RoomType.STAIRCASE) {
            json.put("show", "false");
        }
        else {
            json.put("show", "true");
        }

        double x1 = par.getX1();
        double y1 = par.getY1();
        double x2 = par.getX2();
        double y2 = par.getY2();

        double x = x1;
        double y = y1;

        JSONObject pos = new JSONObject();
        pos.put("x", x);
        pos.put("y", y);
        json.put("pos", pos);

        json.put("fontSize", 1);

        return json;
    }

    public List<JSONObject> getNodes(int nowFloor) {

        List<JSONObject> ans = new LinkedList<>();

        ArrayList<Par> pars = ReadPar.getPar();

        for (int i = 0; i < pars.size(); i++) {

            if (pars.get(i).getmFloor() != nowFloor) continue;

            JSONObject json = new JSONObject();

            Par par = pars.get(i);

            json.put("id", par.getmID());
            json.put("name", par.getmID());

            if (par.getmType() == RoomType.STAIRCASE) {
                json.put("type", "STAIR");
            }
            else if (par.getmType() == RoomType.HALLWAY){
                json.put("type", "HALLWAY");
            }
            else {
                json.put("type", "ROOM");
            }

            json.put("description", "");
            json.put("shopId", -1);

            json.put("labelConfig", getLabelConfig(par));

            json.put("nodeSize", 0);

            ans.add(json);
        }

        return ans;
    }

    /**
     * generate the json of map
     */
    public void outputMapJson() {

        for (int nowFloor = 0; nowFloor < 10; nowFloor++) {
            JSONObject json = new JSONObject();

            json.put("version", "0.5");
            json.put("buildingName", "F");
            json.put("floorNumber", nowFloor);
            json.put("floorId", nowFloor);
            json.put("createDate", "2017-05-25T17:55:16.506Z");

            List<JSONObject> ans = new LinkedList<JSONObject>();

            json.put("regions", getRegions(nowFloor));
            json.put("walls", ans);
            json.put("doors", getDoors(nowFloor));
            json.put("virtualDoors", ans);
            json.put("config", getConfig());

            json.put("nodes", getNodes(nowFloor));


            File file = new File(System.getProperty("user.dir") + "/floor-" + nowFloor + ".json");

            FileWriter fw = null;

            try {
                fw = new FileWriter(file);
                fw.write(json.toString()+ "\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(json.toString());
        }
    }

}
