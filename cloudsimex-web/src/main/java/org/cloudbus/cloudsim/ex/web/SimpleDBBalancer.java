/**
 * 
 */
package org.cloudbus.cloudsim.ex.web;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.ex.disk.HddCloudlet;
import org.cloudbus.cloudsim.ex.disk.HddPe;
import org.cloudbus.cloudsim.ex.disk.HddVm;
import org.cloudbus.cloudsim.ex.util.CustomLog;

/**
 * A simple DB balancer, that allocates a DB cloudlet to the first VM server
 * that has the data to serve it.
 * 
 * @author nikolay.grozev
 * 
 */
public class SimpleDBBalancer implements IDBBalancer {

    private List<HddVm> dbVms;

    /**
     * Constr.
     * 
     * @param dbVms
     *            - The list of DB vms to distribute cloudlets among.
     */
    public SimpleDBBalancer(final List<HddVm> dbVms) {
	this.dbVms = dbVms;
    }

    /**
     * Constr.
     * 
     * @param dbVms
     *            - The list of DB vms to distribute cloudlets among.
     */
    public SimpleDBBalancer(final HddVm... dbVms) {
	this(Arrays.asList(dbVms));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.cloudbus.cloudsim.incubator.web.IDBBalancer#allocateToServer(org.
     * cloudbus.cloudsim.incubator.disk.HddCloudlet)
     */
    @Override
    public void allocateToServer(final HddCloudlet cloudlet) {
	label:
	for (HddVm vm : getVMs()) {
	    for (HddPe hdd : vm.getHost().getHddList()) {
		if (vm.getHddsIds().contains(hdd.getId()) && hdd.containsDataItem(cloudlet.getData().getId())) {
		    cloudlet.setVmId(vm.getId());
		    break label;
		}
	    }
	}

	// If the cloudlet has not yet been assigned a VM
	if (cloudlet.getVmId() == -1) {
	    CustomLog.printf("Cloudlet %d could not be assigned a DB VM, since no VM has its data item %d",
		    cloudlet.getCloudletId(), cloudlet.getData().getId());

	    try {
		cloudlet.setCloudletStatus(Cloudlet.FAILED);
	    } catch (Exception e) {
		CustomLog.logError(Level.SEVERE, "Unexpected error occurred", e);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cloudbus.cloudsim.incubator.web.IDBBalancer#getVMs()
     */
    @Override
    public List<HddVm> getVMs() {
	return dbVms;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.cloudbus.cloudsim.incubator.web.IDBBalancer#setVms(java.util.List)
     */
    @Override
    public void setVms(final List<HddVm> vms) {
	dbVms = vms;
    }

}
