package ca.credits.business.p2b;

import ca.credits.business.AbstractTemplate;
import ca.credits.business.enums.PlatformCodeEnum;
import lombok.Data;

import java.util.Date;

/**
 * Created by chenwen on 16/9/21.
 */
@Data
public class P2bTemplate extends AbstractTemplate {
    /**
     * 唯一主键 , 哈希序列，唯一标识该条记录
     */
    private String primaryKey;

    /**
     * 姓名 , 刘真实
     */
    private String name;

    /**
     * 身份证号 , 43062319920706****
     */
    private String custId;

    /**
     * 手机号 , 18689861***
     */
    private String phone;

    /**
     * qq号 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String qqNum;

    /**
     * 邮箱 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String email;

    /**
     * 居住地址 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String address;

    /**
     * 学校 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String school;

    /**
     * 用户id , pdu7418145516
     */
    private String userId;

    /**
     * 用户昵称 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String nickName;

    /**
     * 平台编码 , nameCode枚举类
     */
    private String platCode;

    /**
     * 不良原因编码 , BadReason枚举类
     */
    private String badFlag;

    /**
     * 发布时间 , yyyy-mm-dd hh:mm:ss, 无法获取该字段，但是依旧保留，取值为空
     */
    private Date publishTime;

    /**
     * 爬取时间 , yyyy-mm-dd hh:mm:ss
     */
    private Date crawlTime;

    /**
     * 删除日期 , yyyy-mm-dd hh:mm:ss
     */
    private Date jrjtDelDt;

    public P2bTemplate(PlatformCodeEnum.INameCode nameCode) {
        super(nameCode);
    }
}
