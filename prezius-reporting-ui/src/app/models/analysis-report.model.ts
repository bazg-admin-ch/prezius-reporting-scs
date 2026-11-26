import { ReportLine } from './ReportLine';

export interface AnalysisReport {
  total: ReportLine;
  CONFORMITY_EVALUATION: ReportLine;
  DETERMINATION_ON_SAMPLES: ReportLine;
  DETERMINATION_ON_MELTING_POINT: ReportLine;
  SAMPLES_FROM_HALLMARKING: ReportLine;
  SAMPLES_FROM_OTHER_PROCESSES: ReportLine;
}
