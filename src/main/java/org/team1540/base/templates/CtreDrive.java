package org.team1540.base.templates;

import org.team1540.base.wrappers.ChickenController;

public interface CtreDrive extends SubsystemAttached {

  public ChickenController getLeftMaster();

  public ChickenController getRightMaster();
}
