package ca.credits.business.xiaohao;

import ca.credits.business.AbstractTemplate;
import ca.credits.business.enums.PlatformCodeEnum;
import lombok.Data;

import java.util.Date;

/**
 * Created by chenwen on 16/9/21.
 */
@Data
public class XiaohaoTemplate extends AbstractTemplate{
    /**
     * 唯一主键 , 哈希序列，唯一标识该条记录
     */
    private String primaryKey;

    /**
     * 手机号 , 15800353170
     */
    private String phone;

    /**
     * 平台编码 , 取值为‘X00001’
     */
    private String platCode;

    /**
     * 爬取时间 , yyyy-mm-dd hh:mm:ss
     */
    private Date crawlTime;

    /**
     * 删除日期 , yyyy-mm-dd hh:mm:ss
     */
    private Date jrjtDelDt;

    public XiaohaoTemplate(PlatformCodeEnum.INameCode nameCode) {
        super(nameCode);
    }
}
