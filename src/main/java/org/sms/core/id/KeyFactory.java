package org.sms.core.id;

/**
 * @author Sunny
 */
public interface KeyFactory {

  /**
   * 根据表名称获取当前记录ID 
   * @param tableName
   * @return
   */
  Long getPrimaryKey(String tableName);

  /**
   * 多租户环境下实现共享schemaer ID获取
   * @param groupName
   * @param tableName
   * @return
   */
  public enum PrimaryKeyType {
    
    /**
     * default use uuid
     * auto 使用数据库自增策略
     */
    DEFAULT("uuid"),CACHEDB("cacheDb"), CLUSTERDB("clusterDb"), AUTO("auto");
    
    public String typeName;
    
    private PrimaryKeyType(String typeName) {
      this.typeName = typeName;
    }
  }
}
