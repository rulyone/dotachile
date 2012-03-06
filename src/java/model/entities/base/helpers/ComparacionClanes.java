/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package model.entities.base.helpers;

/**
 *
 * @author rulyone
 */
public class ComparacionClanes {
    
    private String tagClan1;
    private String tagClan2;
    
    private int winsTorneosClan1;
    private int winsTorneosClan2;
    private int totalTorneosClan1;
    private int totalTorneosClan2;
    
    private int winsClan1VSClan2Torneos;
    private int winsClan2VSClan2Torneos;
    
    private int winsLaddersClan1;
    private int winsLaddersClan2;
    private int totalLaddersClan1;
    private int totalLaddersClan2;
    
    private int winsClan1VSClan2Ladders;
    private int winsClan2VSClan1Ladders;

    public ComparacionClanes(String tagClan1, String tagClan2, int winsTorneosClan1, int winsTorneosClan2, int totalTorneosClan1, int totalTorneosClan2, int winsClan1VSClan2Torneos, int winsClan2VSClan2Torneos, int winsLaddersClan1, int winsLaddersClan2, int totalLaddersClan1, int totalLaddersClan2, int winsClan1VSClan2Ladders, int winsClan2VSClan1Ladders) {
        this.tagClan1 = tagClan1;
        this.tagClan2 = tagClan2;
        this.winsTorneosClan1 = winsTorneosClan1;
        this.winsTorneosClan2 = winsTorneosClan2;
        this.totalTorneosClan1 = totalTorneosClan1;
        this.totalTorneosClan2 = totalTorneosClan2;
        this.winsClan1VSClan2Torneos = winsClan1VSClan2Torneos;
        this.winsClan2VSClan2Torneos = winsClan2VSClan2Torneos;
        this.winsLaddersClan1 = winsLaddersClan1;
        this.winsLaddersClan2 = winsLaddersClan2;
        this.totalLaddersClan1 = totalLaddersClan1;
        this.totalLaddersClan2 = totalLaddersClan2;
        this.winsClan1VSClan2Ladders = winsClan1VSClan2Ladders;
        this.winsClan2VSClan1Ladders = winsClan2VSClan1Ladders;
    }

    public String getTagClan1() {
        return tagClan1;
    }

    public void setTagClan1(String tagClan1) {
        this.tagClan1 = tagClan1;
    }

    public String getTagClan2() {
        return tagClan2;
    }

    public void setTagClan2(String tagClan2) {
        this.tagClan2 = tagClan2;
    }

    public int getTotalLaddersClan1() {
        return totalLaddersClan1;
    }

    public void setTotalLaddersClan1(int totalLaddersClan1) {
        this.totalLaddersClan1 = totalLaddersClan1;
    }

    public int getTotalLaddersClan2() {
        return totalLaddersClan2;
    }

    public void setTotalLaddersClan2(int totalLaddersClan2) {
        this.totalLaddersClan2 = totalLaddersClan2;
    }

    public int getTotalTorneosClan1() {
        return totalTorneosClan1;
    }

    public void setTotalTorneosClan1(int totalTorneosClan1) {
        this.totalTorneosClan1 = totalTorneosClan1;
    }

    public int getTotalTorneosClan2() {
        return totalTorneosClan2;
    }

    public void setTotalTorneosClan2(int totalTorneosClan2) {
        this.totalTorneosClan2 = totalTorneosClan2;
    }

    public int getWinsClan1VSClan2Ladders() {
        return winsClan1VSClan2Ladders;
    }

    public void setWinsClan1VSClan2Ladders(int winsClan1VSClan2Ladders) {
        this.winsClan1VSClan2Ladders = winsClan1VSClan2Ladders;
    }

    public int getWinsClan1VSClan2Torneos() {
        return winsClan1VSClan2Torneos;
    }

    public void setWinsClan1VSClan2Torneos(int winsClan1VSClan2Torneos) {
        this.winsClan1VSClan2Torneos = winsClan1VSClan2Torneos;
    }

    public int getWinsClan2VSClan1Ladders() {
        return winsClan2VSClan1Ladders;
    }

    public void setWinsClan2VSClan1Ladders(int winsClan2VSClan1Ladders) {
        this.winsClan2VSClan1Ladders = winsClan2VSClan1Ladders;
    }

    public int getWinsClan2VSClan2Torneos() {
        return winsClan2VSClan2Torneos;
    }

    public void setWinsClan2VSClan2Torneos(int winsClan2VSClan2Torneos) {
        this.winsClan2VSClan2Torneos = winsClan2VSClan2Torneos;
    }

    public int getWinsLaddersClan1() {
        return winsLaddersClan1;
    }

    public void setWinsLaddersClan1(int winsLaddersClan1) {
        this.winsLaddersClan1 = winsLaddersClan1;
    }

    public int getWinsLaddersClan2() {
        return winsLaddersClan2;
    }

    public void setWinsLaddersClan2(int winsLaddersClan2) {
        this.winsLaddersClan2 = winsLaddersClan2;
    }

    public int getWinsTorneosClan1() {
        return winsTorneosClan1;
    }

    public void setWinsTorneosClan1(int winsTorneosClan1) {
        this.winsTorneosClan1 = winsTorneosClan1;
    }

    public int getWinsTorneosClan2() {
        return winsTorneosClan2;
    }

    public void setWinsTorneosClan2(int winsTorneosClan2) {
        this.winsTorneosClan2 = winsTorneosClan2;
    }
    
    
    
    
}
