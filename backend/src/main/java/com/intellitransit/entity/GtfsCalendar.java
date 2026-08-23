package com.intellitransit.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "gtfs_calendars", indexes = {
    @Index(name = "idx_gtfs_calendar_service_feed", columnList = "gtfs_service_id, feed_id, feed_version"),
    @Index(name = "idx_gtfs_calendar_service_batch", columnList = "gtfs_service_id, batch_id")
})
public class GtfsCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gtfs_service_id", nullable = false, length = 100)
    private String gtfsServiceId;

    @Column(name = "feed_id", nullable = false, length = 50)
    private String feedId = "MTC";

    @Column(name = "feed_version", nullable = false, length = 50)
    private String feedVersion = "2.0";

    @Column(nullable = false)
    private Integer monday;

    @Column(nullable = false)
    private Integer tuesday;

    @Column(nullable = false)
    private Integer wednesday;

    @Column(nullable = false)
    private Integer thursday;

    @Column(nullable = false)
    private Integer friday;

    @Column(nullable = false)
    private Integer saturday;

    @Column(nullable = false)
    private Integer sunday;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "batch_id", nullable = false, length = 64)
    private String batchId;

    public GtfsCalendar() {}

    public GtfsCalendar(Long id, String gtfsServiceId, String feedId, String feedVersion, Integer monday, Integer tuesday, Integer wednesday, Integer thursday, Integer friday, Integer saturday, Integer sunday, LocalDate startDate, LocalDate endDate, String batchId) {
        this.id = id;
        this.gtfsServiceId = gtfsServiceId;
        this.feedId = feedId != null ? feedId : "MTC";
        this.feedVersion = feedVersion != null ? feedVersion : "2.0";
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.startDate = startDate;
        this.endDate = endDate;
        this.batchId = batchId;
    }

    public static GtfsCalendarBuilder builder() {
        return new GtfsCalendarBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGtfsServiceId() { return gtfsServiceId; }
    public void setGtfsServiceId(String gtfsServiceId) { this.gtfsServiceId = gtfsServiceId; }

    public String getFeedId() { return feedId; }
    public void setFeedId(String feedId) { this.feedId = feedId; }

    public String getFeedVersion() { return feedVersion; }
    public void setFeedVersion(String feedVersion) { this.feedVersion = feedVersion; }

    public Integer getMonday() { return monday; }
    public void setMonday(Integer monday) { this.monday = monday; }

    public Integer getTuesday() { return tuesday; }
    public void setTuesday(Integer tuesday) { this.tuesday = tuesday; }

    public Integer getWednesday() { return wednesday; }
    public void setWednesday(Integer wednesday) { this.wednesday = wednesday; }

    public Integer getThursday() { return thursday; }
    public void setThursday(Integer thursday) { this.thursday = thursday; }

    public Integer getFriday() { return friday; }
    public void setFriday(Integer friday) { this.friday = friday; }

    public Integer getSaturday() { return saturday; }
    public void setSaturday(Integer saturday) { this.saturday = saturday; }

    public Integer getSunday() { return sunday; }
    public void setSunday(Integer sunday) { this.sunday = sunday; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public static class GtfsCalendarBuilder {
        private Long id;
        private String gtfsServiceId;
        private String feedId = "MTC";
        private String feedVersion = "2.0";
        private Integer monday;
        private Integer tuesday;
        private Integer wednesday;
        private Integer thursday;
        private Integer friday;
        private Integer saturday;
        private Integer sunday;
        private LocalDate startDate;
        private LocalDate endDate;
        private String batchId;

        public GtfsCalendarBuilder id(Long id) { this.id = id; return this; }
        public GtfsCalendarBuilder gtfsServiceId(String gtfsServiceId) { this.gtfsServiceId = gtfsServiceId; return this; }
        public GtfsCalendarBuilder feedId(String feedId) { this.feedId = feedId; return this; }
        public GtfsCalendarBuilder feedVersion(String feedVersion) { this.feedVersion = feedVersion; return this; }
        public GtfsCalendarBuilder monday(Integer monday) { this.monday = monday; return this; }
        public GtfsCalendarBuilder tuesday(Integer tuesday) { this.tuesday = tuesday; return this; }
        public GtfsCalendarBuilder wednesday(Integer wednesday) { this.wednesday = wednesday; return this; }
        public GtfsCalendarBuilder thursday(Integer thursday) { this.thursday = thursday; return this; }
        public GtfsCalendarBuilder friday(Integer friday) { this.friday = friday; return this; }
        public GtfsCalendarBuilder saturday(Integer saturday) { this.saturday = saturday; return this; }
        public GtfsCalendarBuilder sunday(Integer sunday) { this.sunday = sunday; return this; }
        public GtfsCalendarBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public GtfsCalendarBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public GtfsCalendarBuilder batchId(String batchId) { this.batchId = batchId; return this; }

        public GtfsCalendar build() {
            return new GtfsCalendar(id, gtfsServiceId, feedId, feedVersion, monday, tuesday, wednesday, thursday, friday, saturday, sunday, startDate, endDate, batchId);
        }
    }
}
