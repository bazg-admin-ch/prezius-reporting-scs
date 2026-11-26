import { Observable, of } from 'rxjs';
import { HallmarkingReport } from '../models/HallmarkingReport';
import { AnalysisReport } from '../models/analysis-report.model';
import { ReportLine } from '../models/ReportLine';
import { ReportService } from '../service/report.service';
import { FeatureFlagsService } from '../service/feature-flags.service';
import { FeatureFlags } from '../models/enums/FeatureFlags';

export const fakeReportService: Partial<ReportService> = {
  get hallmarkingReport$(): Observable<HallmarkingReport> {
    return of(createHallmarkingReport());
  },
  get analysisReport$(): Observable<AnalysisReport> {
    return of(createAnalysisReport());
  },
  fetchHallmarkingReport(filterData: string = ''): Observable<HallmarkingReport> {
    return of(createHallmarkingReport());
  },
  fetchAnalysisReport(filterData: string = ''): Observable<AnalysisReport> {
    return of(createAnalysisReport());
  }
};

export const fakeFeatureFlagsService: Partial<FeatureFlagsService> = {
  getFeatureFlags(): Observable<FeatureFlags> {
    return of(createFeatureFlag());
  },
  fetchFeatureFlags(): Observable<FeatureFlags> {
    return of(createFeatureFlag());
  }
};

function createHallmarkingReport(): HallmarkingReport {
  return {
    total: createReportLine(1140, 1141, 1142, 1143, 1144, 1145),
    jewelLaserCertified: createReportLine(1130, 1131, 1132, 1133, 1134, 1135),
    jewelLaserNotCertified: createReportLine(1120, 1121, 1122, 1123, 1124, 1125),
    jewelLaserTotal: createReportLine(1110, 1111, 1112, 1113, 1114, 1115),
    jewelMechanicalCertified: createReportLine(1100, 1101, 1102, 1103, 1104, 1105),
    jewelMechanicalNotCertified: createReportLine(1090, 1091, 1092, 1093, 1094, 1095),
    jewelMechanicalTotal: createReportLine(1080, 1081, 1082, 1083, 1084, 1085),
    jewelTotal: createReportLine(1070, 1071, 1072, 1073, 1074, 1075),
    watchLaserCertified: createReportLine(1060, 1061, 1062, 1063, 1064, 1065),
    watchLaserNotCertified: createReportLine(1050, 1051, 1052, 1053, 1054, 1055),
    watchLaserTotal: createReportLine(1040, 1041, 1042, 1043, 1044, 1045),
    watchMechanicalCertified: createReportLine(1030, 1031, 1032, 1033, 1034, 1035),
    watchMechanicalNotCertified: createReportLine(1020, 1021, 1022, 1023, 1024, 1025),
    watchMechanicalTotal: createReportLine(1010, 1011, 1012, 1013, 1014, 1015),
    watchTotal: createReportLine(1000, 1001, 1002, 1003, 1004, 1005)
  };
}

function createAnalysisReport() {
  return {
    total: createReportLine(600, 610, 620, 630, 640, 650),
    CONFORMITY_EVALUATION: null,
    DETERMINATION_ON_SAMPLES: createReportLine(400, 410, 420, 430, 440, 450),
    DETERMINATION_ON_MELTING_POINT: createReportLine(300, 310, 320, 330, 340, 350),
    SAMPLES_FROM_HALLMARKING: createReportLine(200, 210, 220, 230, 240, 250),
    SAMPLES_FROM_OTHER_PROCESSES: createReportLine(100, 110, 120, 130, 140, 150)
  };
}

function createReportLine(
  ag: number,
  au: number,
  pd: number,
  pt: number,
  total: number,
  totalCostsWithVat: number
): ReportLine {
  return {
    ag: ag,
    au: au,
    pd: pd,
    pt: pt,
    total: total,
    totalCostsWithVat: totalCostsWithVat
  };
}

function createFeatureFlag(): FeatureFlags {
  return {
    PREZIUS_3_0: true
  };
}
