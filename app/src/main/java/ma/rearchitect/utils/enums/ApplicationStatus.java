package opswat.com.enums;

/**
 * Created by LenVo on 7/18/18.
 */

public enum  ApplicationStatus {
    INPROGRESS(0),
    CLEAN(1),
    SUSPECT(2),
    INFECTED(3),
    UNKNOWN(4);

    ApplicationStatus(int status) {
    }
}
