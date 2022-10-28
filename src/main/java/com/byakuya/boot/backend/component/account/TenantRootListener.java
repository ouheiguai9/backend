//package com.byakuya.boot.backend.component.account;
//
//import com.byakuya.boot.backend.component.unique.Type;
//import com.byakuya.boot.backend.component.unique.UniqueService;
//import com.byakuya.boot.backend.exception.BackendException;
//import com.byakuya.boot.backend.exception.ErrorStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.context.annotation.Lazy;
//
//import javax.persistence.PrePersist;
//
///**
// * Created by 田伯光 at 2022/10/9 19:05
// */
//@Configurable
//public class TenantRootListener {
//
//    private UniqueService uniqueService;
//
//    @Autowired
//    @Lazy
//    public void setUniqueService(UniqueService uniqueService) {
//        this.uniqueService = uniqueService;
//    }
//
//    @PrePersist
//    public void prePersist(Object target) {
//        if (!(target instanceof Account)) return;
//        Account account = (Account) target;
//        if (account.isRoot()) {
//            String uniqueValue = "root";
//            if (uniqueService.checkUnique(account.getTenantId(), Type.TENANT, uniqueValue)) {
//                throw new BackendException(ErrorStatus.TENANT_ROOT_EXIST);
//            }
//            try {
//                uniqueService.addUnique(account.getTenantId(), Type.TENANT, uniqueValue);
//            } catch (Exception e) {
//                throw new BackendException(ErrorStatus.TENANT_ROOT_EXIST, e);
//            }
//        }
//    }
//}
