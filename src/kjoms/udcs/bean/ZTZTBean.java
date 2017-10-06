package kjoms.udcs.bean;

import java.io.Serializable;

/**
 * Created by panpan on 2016/11/24.
 */
public class ZTZTBean implements Serializable{
    private String ID;//资源Id
    private String ZTZT_ID;//专题图id
    private String name;
    private String title;
    private String orgnizationName;
    private String individualName;
    private String fzrOrgName;
    private String address;
    private String voice;
    private String fax;
    private String directoryId;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrgnizationName() {
        return orgnizationName;
    }

    public void setOrgnizationName(String orgnizationName) {
        this.orgnizationName = orgnizationName;
    }

    public String getIndividualName() {
        return individualName;
    }

    public void setIndividualName(String individualName) {
        this.individualName = individualName;
    }

    public String getFzrOrgName() {
        return fzrOrgName;
    }

    public void setFzrOrgName(String fzrOrgName) {
        this.fzrOrgName = fzrOrgName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
    }

    public String getZTZT_ID() {
        return ZTZT_ID;
    }

    public void setZTZT_ID(String ZTZT_ID) {
        this.ZTZT_ID = ZTZT_ID;
    }
}
