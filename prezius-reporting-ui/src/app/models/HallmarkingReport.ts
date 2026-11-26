import { ReportLine } from './ReportLine';

export interface HallmarkingReport {
  total: ReportLine;
  watchTotal: ReportLine;
  watchLaserTotal: ReportLine;
  watchMechanicalTotal: ReportLine;
  jewelTotal: ReportLine;
  jewelLaserTotal: ReportLine;
  jewelMechanicalTotal: ReportLine;

  watchLaserCertified: ReportLine;
  watchLaserNotCertified: ReportLine;
  watchMechanicalCertified: ReportLine;
  watchMechanicalNotCertified: ReportLine;
  jewelLaserCertified: ReportLine;
  jewelLaserNotCertified: ReportLine;
  jewelMechanicalCertified: ReportLine;
  jewelMechanicalNotCertified: ReportLine;
}
