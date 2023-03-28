package vedomosti;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import service.LoggingService;

/**
 * Группировка организаций по ключам
 *
 * @author User
 */
public class OrganizationsGroup {

    private TreeMap<String, ArrayList<Organization>> orgs;

    public OrganizationsGroup() {
        orgs = new TreeMap<>();
    }

    public void addOrg(Organization org) {
        String orgKey = org.getComplexKey();
        if (orgs.containsKey(orgKey) == false) {
            ArrayList<Organization> suborgs = new ArrayList<>();
            orgs.put(orgKey, suborgs);
        }
        orgs.get(orgKey).add(org);
    }

    public void allocateOrgGroup(OrganizationsGroup orgsGroup) {
        for (String orgKey : orgsGroup.orgs.keySet()) {
            for (Organization object : orgsGroup.orgs.get(orgKey)) {
                addOrg(object);
            }
        }
    }

    public void merge() {
        for (String orgKeys : orgs.keySet()) {
            if (orgs.get(orgKeys).size() > 1) {
                ArrayList<Organization> swapList = new ArrayList<>();
                ArrayList<Organization> mergingOrgsList = orgs.get(orgKeys);
                LoggingService.writeLog("will merge orgs with key: " + mergingOrgsList.get(0).getComplexKey(), "debug");
                Organization rootOrg = mergingOrgsList.get(0);
                for (int i = 1; i < mergingOrgsList.size(); i++) {
                    rootOrg.consumeOrganization(mergingOrgsList.get(i));
                }
                swapList.add(rootOrg);
                orgs.put(orgKeys, swapList);
            }
        }
    }

    public void print() {
        LoggingService.writeLog(">> OrganizationsGroup print", "debug");
        for (String key : orgs.keySet()) {
            LoggingService.writeLog(">> key: " + key + "  size: " + orgs.get(key).size(), "debug");
        }
    }

    public HashSet<String> getUniqueAdditionalKeys() {
        HashSet<String> differentOrgsKeys = new HashSet<>();
        for (String orgKey : orgs.keySet()) {
            String keyPart = orgKey.substring(orgKey.indexOf("-"));
            differentOrgsKeys.add(keyPart);
        }
        return differentOrgsKeys;
    }

    public void fillAll(TreeMap<String, ArrayList<Organization>> orgs) {
        this.orgs = orgs;
    }

    public TreeMap<String, ArrayList<Organization>> getAllOrgsByAdditionalKey(String addKey) {
        TreeMap<String, ArrayList<Organization>> map = new TreeMap<>();
        for (String key : orgs.keySet()) {
            if (key.contains(addKey)) {
                map.put(key, orgs.get(key));
            }
        }
        return map;
    }

    public ArrayList<Organization> getOrgList() {
        ArrayList<Organization> list = new ArrayList<>();
        for (ArrayList<Organization> arLIst : orgs.values()) {
            list.addAll(arLIst);
        }
        return list;
    }
}
