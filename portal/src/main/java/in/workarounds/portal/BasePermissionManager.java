package in.workarounds.portal;

/**
 * Created by madki on 29/11/15.
 */
public interface BasePermissionManager {
    void promptForPermission();
    void onPermissionApproved();
    void onPermissionDenied();
}
