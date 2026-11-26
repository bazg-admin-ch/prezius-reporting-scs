import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { appEnvironment } from '../../environments/environment';
import { AnalysisReport } from '../models/analysis-report.model';
import { HallmarkingReport } from '../models/HallmarkingReport';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private httpClient = inject(HttpClient);

  private hallmarkingReport: BehaviorSubject<HallmarkingReport> = new BehaviorSubject<HallmarkingReport>(null);
  private analysisReport: BehaviorSubject<AnalysisReport> = new BehaviorSubject<AnalysisReport>(null);

  get hallmarkingReport$(): Observable<HallmarkingReport> {
    return this.hallmarkingReport.asObservable();
  }

  get analysisReport$(): Observable<AnalysisReport> {
    return this.analysisReport.asObservable();
  }

  fetchHallmarkingReport(filterData = ''): Observable<HallmarkingReport> {
    return this.httpClient
      .get<HallmarkingReport>(`${appEnvironment.BACKEND_SERVICE_API}api/ui/hallmarking-reports?${filterData}`)
      .pipe(
        tap(report => {
          this.hallmarkingReport.next(report);
        })
      );
  }

  fetchAnalysisReport(filterData = ''): Observable<AnalysisReport> {
    return this.httpClient
      .get<AnalysisReport>(`${appEnvironment.BACKEND_SERVICE_API}api/ui/analysis-reports?${filterData}`)
      .pipe(
        tap(report => {
          this.analysisReport.next(report);
        })
      );
  }

  downloadCsv(filterData: string = '') {
    this.httpClient
      .get(`${appEnvironment.BACKEND_SERVICE_API}api/ui/reports/export?${filterData}`, {
        responseType: 'arraybuffer'
      })
      .subscribe(response => this.downloadFile(response, 'application/csv'));
  }

  downloadFile(data: any, type: string) {
    const fileName = 'report.csv';
    const element = document.createElement('a');
    document.body.appendChild(element);
    element.style.display = 'none';
    const blob = new Blob([data], { type: type });
    const url = window.URL.createObjectURL(blob);
    element.href = url;
    element.download = fileName;
    element.click();
    window.URL.revokeObjectURL(url);
    element.remove();
  }
}
