package io.j1st.power.storage.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import io.j1st.power.storage.mongo.entity.Permission;
import org.apache.commons.configuration.AbstractConfiguration;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.include;


/**
 * Created by Administrator on 2016/4/27.
 */
public class MongoStorage {

    protected MongoClient client;
    protected MongoDatabase database;

    public void init(AbstractConfiguration config) {
        // MongoClient
        List<ServerAddress> addresses = parseAddresses(config.getString("mongo.address"));
        List<MongoCredential> credentials = parseCredentials(
                config.getString("mongo.userName"),
                "admin",
                config.getString("mongo.password"));
        if (addresses.size() == 1) {
            this.client = new MongoClient(addresses.get(0), credentials);
        } else {
            this.client = new MongoClient(addresses, credentials);
        }
        this.database = this.client.getDatabase(config.getString("mongo.database"));
    }


    public void destroy() {
        if (this.client != null) this.client.close();
    }

    private ServerAddress parseAddress(String address) {
        int idx = address.indexOf(':');
        return (idx == -1) ?
                new ServerAddress(address) :
                new ServerAddress(address.substring(0, idx), Integer.parseInt(address.substring(idx + 1)));
    }

    private List<ServerAddress> parseAddresses(String addresses) {
        List<ServerAddress> result = new ArrayList<>();
        String[] addrs = addresses.split(" *, *");
        for (String addr : addrs) {
            result.add(parseAddress(addr));
        }
        return result;
    }

    private List<MongoCredential> parseCredentials(String userName, String database, String password) {
        List<MongoCredential> result = new ArrayList<>();
        result.add(MongoCredential.createCredential(userName, database, password.toCharArray()));
        return result;
    }


    /* =========================================== Agent Operations ===============================================*/


    /**
     * 判断 采集器 权限
     *
     * @param id         采集器Id
     * @param permission 最低权限
     * @return True 权限满足
     */
    public boolean isAgentdByUser(String id, Permission permission) {
        if (!ObjectId.isValid(id)) {
            return false;
        }
        return this.database.getCollection("agents")
                .find(and(eq("_id", new ObjectId(id)), eq("permissions", new Document("$elemMatch", new Document()
                        .append("user_id", permission.getUserId())))))
                .first() != null;
    }


    /**
     * 判断Agent是否存在
     *
     * @param id 采集器Id
     * @return 采集器 or Null
     */
    public boolean isAgentExists(String id) {
        if (!ObjectId.isValid(id)) {
            return false;
        }
        return this.database.getCollection("agents")
                .find(eq("_id", new ObjectId(id))).first() != null;
    }


    /**
     * 判断Agent是否存在
     *
     * @param userName 采集器Id
     * @return 采集器 or Null
     */
    public boolean isAgentAuth(String userName, String password) {
        if (!ObjectId.isValid(userName)) {
            return false;
        }
        return this.database.getCollection("agents")
                .find(and(eq("_id", new ObjectId(userName)), eq("token", password))).first() != null;
    }


    /**
     * 获取 用户信息，根据 Token
     *
     * @param token Token
     * @return 用户信息 or Null
     */
    public String getUserByToken(String token) {
        Document d = this.database.getCollection("users")
                .find(eq("token", token))
                .projection(exclude("password"))
                .first();
        if (d == null) return null;
        return d.getObjectId("_id").toString();
    }

    /**
     * 获取 产品 是否被激活
     * 激活的定义为：旗下采集器至少有一个被激活
     *
     * @param productId 产品Id
     * @return True 被激活
     */
    public boolean isProductActivated(String productId) {
        if (!ObjectId.isValid(productId)) {
            return false;
        }
        return this.database.getCollection("agents")
                .find(and(eq("product_id", new ObjectId(productId)), exists("activated_at", true)))
                .projection(include("_id"))
                .first() != null;

    }


    /**
     * 获取 产品 是否被激活
     * 激活的定义为：旗下采集器至少有一个被激活
     *
     * @param agentId
     * @return True 被激活
     */
    public Integer getProductStatusByAgentId(String agentId) {
        Integer status = null;
        if (!ObjectId.isValid(agentId)) {
            return null;
        }
        Document agentDocument = this.database.getCollection("agents")
                .find(eq("_id", new ObjectId(agentId)))
                .first();
        if (agentDocument != null) {
            ObjectId productId = agentDocument.getObjectId("product_id");
            if (productId != null) {
                Document productDocument = this.database.getCollection("products")
                        .find(eq("_id", productId))
                        .first();
                if (productDocument != null) {
                    status = productDocument.getInteger("status");
                }
            }
        }

        return status;
    }

    /**
     * 根据Operator订购的服务类型来查询可用数量(未过期的)
     *
     * @param serviceType service type
     * @param operatorId  operator ID
     * @return number of service
     */
    public long getServiceCountByOperatorId(Integer serviceType, ObjectId operatorId) {
        long count = 0;
        Document query = new Document();
        query.append("user_id", operatorId);
        query.append("serviceType", serviceType);
        query.append("expired_at", new Document("$gte", new Date()));
        FindIterable<Document> ds = this.database.getCollection("user_services").find(query);
        if (ds != null) {
            for (Document d : ds) {
                if (d.getLong("used") != null) {
                    count += d.getLong("count") - d.getLong("used");
                } else {
                    count += d.getLong("count");
                }
            }
        }
        return count;
    }

    /**
     * Get Operator Id
     *
     * @param agentId Agent id
     * @return Operator id
     */
    public String getOperatorIdByAgent(String agentId) {
        if (!ObjectId.isValid(agentId)) {
            return null;
        }
        Document doc = this.database.getCollection("user_assets_info").find(eq("agent_id", new ObjectId(agentId)))
                .projection(include("user_id"))
                .first();
        if (doc == null) return null;
        return doc.getObjectId("user_id").toString();
    }

}
