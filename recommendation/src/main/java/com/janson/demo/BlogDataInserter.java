package com.janson.demo;

import com.google.gson.JsonObject;
import io.prediction.EngineClient;
import io.prediction.EventClient;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * description: 通过predictionIO构建简单的商品推荐系统
 * @author: Janson
 * @version: 1.0
 * @date: 2018/10/15
 */
public class BlogDataInserter {

    private static List<Bean> usrList;
    private static List<Bean> itemList;
    private static String[] actions = new String[]{"buy","view","comment","interest","no interest"};

    private static final String API_KEY = "wwoTLn0FR7vH6k51Op8KbU1z4tqeFGZyvBpSgafOaSSe40WqdMf90lEncOA0SB13";

    public static void main(String[] args) throws Exception {

        initUserAndItemList();

        EventClient client = new EventClient(API_KEY);

        //添加用户、商品、行为到事件服务器
        addUsers(client);
        addItems(client);
        userItemAction(client);
        client.close();

        //创建引擎客户端，map输入条件
        EngineClient engineClient = new EngineClient();
        HashMap<String, Object> map = new HashMap<>();
        map.put("url","http://localhost:7070");
        map.put("accesskey",API_KEY);
        JsonObject jsonObject = engineClient.sendQuery(map);

        System.out.println(jsonObject.toString());
    }

    private static void addItems(EventClient client) throws InterruptedException, ExecutionException, IOException {
        for (Bean bean : itemList){
            client.setItem(bean.getId(), bean.getDetails(), new DateTime());
        }
    }

    private static void addUsers(EventClient client) throws InterruptedException, ExecutionException, IOException {
        for (Bean bean : usrList){
            client.setUser(bean.getId(), bean.getDetails(), new DateTime());
        }
    }

    private static void initUserAndItemList() {
        usrList = new ArrayList<Bean>(10);
        for (int cnt=1; cnt<=usrList.size(); cnt++){
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId","user"+cnt);
            map.put("name","mr."+cnt);
            map.put("money","10000");

            usrList.add(new Bean("user"+cnt, map));
        }
        itemList = new ArrayList<Bean>(100);
        for (int cnt=1; cnt<itemList.size(); cnt++){
            HashMap<String, Object> map = new HashMap<>();
            map.put("itemId","item"+0);
            map.put("itemName","iName"+0);
            map.put("price","100");
            itemList.add(new Bean("item"+cnt, map));
        }
    }



    private static void userItemAction(EventClient client) throws Exception {
        Random random = new Random();
        for (int cnt=0; cnt<100; cnt++){
            String action = actions[random.nextInt(actions.length)];
            String uid = usrList.get(random.nextInt(usrList.size())).getId();
            String iid = itemList.get(random.nextInt(itemList.size())).getId();

            client.userActionItem(action, uid, iid, new HashMap<>(1), new DateTime());
        }
    }

}

class Bean{
    private String id;
    private Map details;

    public Bean(String id, Map details) {
        this.id = id;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public Map getDetails() {
        return details;
    }
}