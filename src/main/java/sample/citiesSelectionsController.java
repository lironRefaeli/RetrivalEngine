package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class citiesSelectionsController{

    //ObservableList<String> citiesOptions = FXCollections.observableArrayList("London","Paris");
    @FXML
    Button chooseButton;
    @FXML
    ListView citiesList;

    @FXML
    CheckBox ABUJACheckBox;
    @FXML
    CheckBox ACCRACheckBox;
    @FXML
    CheckBox ALGIERSCheckBox;
    @FXML
    CheckBox ALOFICheckBox;
    @FXML
    CheckBox AMMANCheckBox;
    @FXML
    CheckBox AMSTERDAMCheckBox;
    @FXML
    CheckBox ANKARACheckBox;
    @FXML
    CheckBox ANTANANARIVOCheckBox;
    @FXML
    CheckBox APIACheckBox;
    @FXML
    CheckBox ASHGABATCheckBox;
    @FXML
    CheckBox ASMARACheckBox;
    @FXML
    CheckBox ATHENSCheckBox;
    @FXML
    CheckBox AVARUACheckBox;
    @FXML
    CheckBox BAGHDADCheckBox;
    @FXML
    CheckBox BAKUCheckBox;
    @FXML
    CheckBox BAMAKOCheckBox;
    @FXML
    CheckBox BANGKOKCheckBox;
    @FXML
    CheckBox BANGUICheckBox;
    @FXML
    CheckBox BANJULCheckBox;
    @FXML
    CheckBox BASSETERRECheckBox;
    @FXML
    CheckBox BEIJINGCheckBox;
    @FXML
    CheckBox BEIRUTCheckBox;
    @FXML
    CheckBox BELGRADECheckBox;
    @FXML
    CheckBox BELMOPANCheckBox;
    @FXML
    CheckBox BERLINCheckBox;
    @FXML
    CheckBox BERNCheckBox;
    @FXML
    CheckBox BISHKEKCheckBox;
    @FXML
    CheckBox BISSAUCheckBox;
    @FXML
    CheckBox BRATISLAVACheckBox;
    @FXML
    CheckBox BRAZZAVILLECheckBox;
    @FXML
    CheckBox BRIDGETOWNCheckBox;
    @FXML
    CheckBox BRUSSELSCheckBox;
    @FXML
    CheckBox BUCHARESTCheckBox;
    @FXML
    CheckBox BUDAPESTCheckBox;
    @FXML
    CheckBox BUJUMBURACheckBox;
    @FXML
    CheckBox CAIROCheckBox;
    @FXML
    CheckBox CANBERRACheckBox;
    @FXML
    CheckBox CARACASCheckBox;
    @FXML
    CheckBox CASTRIESCheckBox;
    @FXML
    CheckBox CAYENNECheckBox;
    @FXML
    CheckBox COLOMBOCheckBox;
    @FXML
    CheckBox CONAKRYCheckBox;
    @FXML
    CheckBox COPENHAGENCheckBox;
    @FXML
    CheckBox DAKARCheckBox;
    @FXML
    CheckBox DAMASCUSCheckBox;
    @FXML
    CheckBox DHAKACheckBox;
    @FXML
    CheckBox DILICheckBox;
    @FXML
    CheckBox DJIBOUTICheckBox;
    @FXML
    CheckBox DODOMACheckBox;
    @FXML
    CheckBox DOHACheckBox;
    @FXML
    CheckBox DOUGLASCheckBox;
    @FXML
    CheckBox DUBLINCheckBox;
    @FXML
    CheckBox DUSHANBECheckBox;
    @FXML
    CheckBox FREETOWNCheckBox;
    @FXML
    CheckBox GABORONECheckBox;
    @FXML
    CheckBox GEORGETOWNCheckBox;
    @FXML
    CheckBox GIBRALTARCheckBox;
    @FXML
    CheckBox GUSTAVIACheckBox;
    @FXML
    CheckBox HAMILTONCheckBox;
    @FXML
    CheckBox HANOICheckBox;
    @FXML
    CheckBox HARARE;
    @FXML
    CheckBox HAVANA;
    @FXML
    CheckBox HELSINKI;
    @FXML
    CheckBox HONIARA;
    @FXML
    CheckBox ISLAMABAD;
    @FXML
    CheckBox JAKARTA;
    @FXML
    CheckBox JAMESTOWN;
    @FXML
    CheckBox JERUSALEM;
    @FXML
    CheckBox JUBA;
    @FXML
    CheckBox KABUL;
    @FXML
    CheckBox KAMPALA;
    @FXML
    CheckBox KATHMANDU;
    @FXML
    CheckBox KHARTOUM;
    @FXML
    CheckBox KIEV;
    @FXML
    CheckBox KIGALI;
    @FXML
    CheckBox KINGSTON;
    @FXML
    CheckBox KINGSTOWN;
    @FXML
    CheckBox KINSHASA;
    @FXML
    CheckBox LIBREVILLE;
    @FXML
    CheckBox LILONGWE;
    @FXML
    CheckBox LIMA;
    @FXML
    CheckBox LISBON;
    @FXML
    CheckBox LJUBLJANA;
    @FXML
    CheckBox LOBAMBA;
    @FXML
    CheckBox LONDON;
    @FXML
    CheckBox LUANDA;
    @FXML
    CheckBox LUSAKA;
    @FXML
    CheckBox LUXEMBOURG;
    @FXML
    CheckBox MADRID;
    @FXML
    CheckBox MAJURO;
    @FXML
    CheckBox MALABO;
    @FXML
    CheckBox MANAGUA;
    @FXML
    CheckBox MANAMA;
    @FXML
    CheckBox MANILA;
    @FXML
    CheckBox MAPUTO;
    @FXML
    CheckBox MARIEHAMN;
    @FXML
    CheckBox MARIGOT;
    @FXML
    CheckBox MASERU;
    @FXML
    CheckBox MINSK;
    @FXML
    CheckBox MOGADISHU;
    @FXML
    CheckBox MONACO;
    @FXML
    CheckBox MONROVIA;
    @FXML
    CheckBox MONTEVIDEO;
    @FXML
    CheckBox MORONI;
    @FXML
    CheckBox MOSCOW;
    @FXML
    CheckBox MUSCAT;
    @FXML
    CheckBox NAIROBI;
    @FXML
    CheckBox NASSAU;
    @FXML
    CheckBox NAYPYIDAW;
    @FXML
    CheckBox NIAMEY;
    @FXML
    CheckBox NICOSIA;
    @FXML
    CheckBox NOUAKCHOTT;
    @FXML
    CheckBox NUUK;
    @FXML
    CheckBox ORANJESTAD;
    @FXML
    CheckBox OSLO;
    @FXML
    CheckBox OTTAWA;
    @FXML
    CheckBox OUAGADOUGOU;
    @FXML
    CheckBox PARAMARIBO;
    @FXML
    CheckBox PARIS;
    @FXML
    CheckBox PHILIPSBURG;
    @FXML
    CheckBox PLYMOUTH;
    @FXML
    CheckBox PODGORICA;
    @FXML
    CheckBox PRAGUE;
    @FXML
    CheckBox PRAIA;
    @FXML
    CheckBox PRETORIA;
    @FXML
    CheckBox PRISTINA;
    @FXML
    CheckBox PYONGYANG;
    @FXML
    CheckBox QUITO;
    @FXML
    CheckBox RABAT;
    @FXML
    CheckBox RAMALLAH;
    @FXML
    CheckBox RIGA;
    @FXML
    CheckBox RIYADH;
    @FXML
    CheckBox ROME;
    @FXML
    CheckBox ROSEAU;
    @FXML
    CheckBox SAINTDENIS;
    @FXML
    CheckBox AINTPIERRE;
    @FXML
    CheckBox SAIPAN;
    @FXML
    CheckBox SANTIAGO;
    @FXML
    CheckBox SARAJEVO;
    @FXML
    CheckBox SEOUL;
    @FXML
    CheckBox SINGAPORE;
    @FXML
    CheckBox SKOPJE;
    @FXML
    CheckBox SOFIA;
    @FXML
    CheckBox STANLEY;
    @FXML
    CheckBox STOCKHOLM;
    @FXML
    CheckBox SUCRE;
    @FXML
    CheckBox SUVA;
    @FXML
    CheckBox TAIPEI;
    @FXML
    CheckBox TALLINN;
    @FXML
    CheckBox TASHKENT;
    @FXML
    CheckBox TBILISI;
    @FXML
    CheckBox TEGUCIGALPA;
    @FXML
    CheckBox TEHRAN;
    @FXML
    CheckBox THIMPHU;
    @FXML
    CheckBox TIRANA;
    @FXML
    CheckBox TOKYO;
    @FXML
    CheckBox TRIPOLI;
    @FXML
    CheckBox TUNIS;
    @FXML
    CheckBox VADUZ;
    @FXML
    CheckBox VALLETTA;
    @FXML
    CheckBox VICTORIA;
    @FXML
    CheckBox VIENNA;
    @FXML
    CheckBox VIENTIANE;
    @FXML
    CheckBox VILNIUS;
    @FXML
    CheckBox WARSAW;
    @FXML
    CheckBox WELLINGTON;
    @FXML
    CheckBox WILLEMSTAD;
    @FXML
    CheckBox WINDHOEK;
    @FXML
    CheckBox YAMOUSSOUKRO;
    @FXML
    CheckBox YEREVAN;
    @FXML
    CheckBox ZAGREB;


    public void chooseCities(ActionEvent event) {

        boolean ABUJA =  ABUJACheckBox.isSelected();
        boolean ACCRA =  ACCRACheckBox.isSelected();
        boolean ALGIERS = ALGIERSCheckBox.isSelected();
        boolean ALOFI = ALOFICheckBox.isSelected();
        boolean AMMAN = AMMANCheckBox.isSelected();
        boolean AMSTERDAM =  AMSTERDAMCheckBox.isSelected();
        boolean ANKARA = ANKARACheckBox.isSelected();
        boolean ANTANANARIV = ANTANANARIVOCheckBox.isSelected();
        boolean APIA =  APIACheckBox.isSelected();
        boolean ASHGABAT = ASHGABATCheckBox.isSelected();
        boolean ASMARA = ASMARACheckBox.isSelected();
        boolean ATHENS = ATHENSCheckBox.isSelected();
        boolean AVARUA=  AVARUACheckBox.isSelected();
        boolean BAGHDAD=  BAGHDADCheckBox.isSelected();
        boolean BAKU=  BAKUCheckBox.isSelected();
        boolean BAMAKO=  BAMAKOCheckBox.isSelected();
        boolean BANGKOK=  BANGKOKCheckBox.isSelected();
        boolean BANGUI=  BANGUICheckBox.isSelected();
        boolean BANJUL=  BANJULCheckBox.isSelected();
        boolean BASSETERRE =  BASSETERRECheckBox.isSelected();
        boolean BEIJING=  BEIJINGCheckBox.isSelected();
        boolean BEIRUT=  BEIRUTCheckBox.isSelected();
        boolean BELGRADE=  BELGRADECheckBox.isSelected();
        boolean BELMOPAN=  BELMOPANCheckBox.isSelected();
        boolean BERLIN=  BERLINCheckBox.isSelected();
        boolean BERN=  BERNCheckBox.isSelected();
        boolean BISHKEK=  BISHKEKCheckBox.isSelected();
        boolean BISSAU=  BISSAUCheckBox.isSelected();
        boolean BRATISLAVA=  BRATISLAVACheckBox.isSelected();
        boolean BRAZZAVILLE=  BRAZZAVILLECheckBox.isSelected();
        boolean BRIDGETOWN=  BRIDGETOWNCheckBox.isSelected();
        boolean BRUSSELS=  BRUSSELSCheckBox.isSelected();
        boolean BUCHAREST=  BUCHARESTCheckBox.isSelected();
        boolean BUDAPEST=  BUDAPESTCheckBox.isSelected();
        boolean BUJUMBURA =  BUJUMBURACheckBox.isSelected();
        boolean CAIRO=  CAIROCheckBox.isSelected();
        boolean CANBERRA=  CANBERRACheckBox.isSelected();
        boolean CARACAS=  CARACASCheckBox.isSelected();
        boolean CASTRIES=  CASTRIESCheckBox.isSelected();
        boolean CAYENNE=  CAYENNECheckBox.isSelected();
        boolean COLOMBO=  COLOMBOCheckBox.isSelected();
        boolean CONAKRY=  CONAKRYCheckBox.isSelected();
        boolean COPENHAGEN=  COPENHAGENCheckBox.isSelected();
        boolean DAKAR=  DAKARCheckBox.isSelected();
        boolean DAMASCUS=  DAMASCUSCheckBox.isSelected();
        boolean DHAKA=  DHAKACheckBox.isSelected();
        boolean DILI=  DILICheckBox.isSelected();
        boolean DJIBOUTI=  DJIBOUTICheckBox.isSelected();
        boolean DODOMA=   DODOMACheckBox.isSelected();
        boolean DOHA=  DOHACheckBox.isSelected();
        boolean DOUGLAS=  DOUGLASCheckBox.isSelected();
        boolean DUBLIN=   DUBLINCheckBox.isSelected();
        boolean DUSHANBE=  DUSHANBECheckBox.isSelected();
        boolean FREETOWN=  FREETOWNCheckBox.isSelected();
        boolean GABORONE=  GABORONECheckBox.isSelected();
        boolean GEORGETOWN=  GEORGETOWNCheckBox.isSelected();
        boolean GIBRALTAR=  GIBRALTARCheckBox.isSelected();
        boolean GUSTAVIA=  GUSTAVIACheckBox.isSelected();
        boolean HAMILTON=  HAMILTONCheckBox.isSelected();
        boolean HANOI=  HANOICheckBox.isSelected();
        boolean HARAREc=  HARARE.isSelected();
        boolean HAVANAc=  HAVANA.isSelected();
        boolean HELSINKIc=  HELSINKI.isSelected();
        boolean HONIARAc=  HONIARA.isSelected();
        boolean ISLAMABADc=  ISLAMABAD.isSelected();
        boolean JAKARTAc=  JAKARTA.isSelected();
        boolean JAMESTOWNc=  JAMESTOWN.isSelected();
        boolean JERUSALEMc=  JERUSALEM.isSelected();
        boolean JUBAc=  JUBA.isSelected();
        boolean KABULc=  KABUL.isSelected();
        boolean KAMPALAc=  KAMPALA.isSelected();
        boolean KATHMANDUc=  KATHMANDU.isSelected();
        boolean KHARTOUMc=  KHARTOUM.isSelected();
        boolean KIEVc=  KIEV.isSelected();
        boolean KIGALIc=  KIGALI.isSelected();
        boolean KINGSTONc=  KINGSTON.isSelected();
        boolean KINGSTOWNc=  KINGSTOWN.isSelected();
        boolean KINSHASAc=  KINSHASA.isSelected();
        boolean LIBREVILLEc=  LIBREVILLE.isSelected();
        boolean LILONGWEc=  LILONGWE.isSelected();
        boolean LIMAc=  LIMA.isSelected();
        boolean LISBONc=  LISBON.isSelected();
        boolean LJUBLJANAc=  LJUBLJANA.isSelected();
        boolean LOBAMBAc=  LOBAMBA.isSelected();
        boolean LONDONc=  LONDON.isSelected();
        boolean LUANDAc=  LUANDA.isSelected();
        boolean LUSAKAc=  LUSAKA.isSelected();
        boolean LUXEMBOURGc=  LUXEMBOURG.isSelected();
        boolean MADRIDc=  MADRID.isSelected();
        boolean MAJUROc=  MAJURO.isSelected();
        boolean MALABOc=  MALABO.isSelected();
        boolean MANAGUAc=  MANAGUA.isSelected();
        boolean MANAMAc=  MANAMA.isSelected();
        boolean MANILAc=  MANILA.isSelected();
        boolean MAPUTOc=  MAPUTO.isSelected();
        boolean MARIEHAMNc=  MARIEHAMN.isSelected();
        boolean MARIGOTc=  MARIGOT.isSelected();
        boolean MASERUc=  MASERU.isSelected();
        boolean MINSKc=  MINSK.isSelected();
        boolean MOGADISHUc=  MOGADISHU.isSelected();
        boolean MONACOc=  MONACO.isSelected();
        boolean MONROVIAc=  MONROVIA.isSelected();
        boolean MONTEVIDEOc=  MONTEVIDEO.isSelected();
        boolean MORONIc=  MORONI.isSelected();
        boolean MOSCOWc=  MOSCOW.isSelected();
        boolean MUSCATc=  MUSCAT.isSelected();
        boolean NAIROBIc=  NAIROBI.isSelected();
        boolean NASSAUc=  NASSAU.isSelected();
        boolean NAYPYIDAWc=  NAYPYIDAW.isSelected();
        boolean NIAMEYc=  NIAMEY.isSelected();
        boolean NICOSIAc=  NICOSIA.isSelected();
        boolean NOUAKCHOTTc=  NOUAKCHOTT.isSelected();
        boolean NUUKc=  NUUK.isSelected();
        boolean ORANJESTADc=  ORANJESTAD.isSelected();
        boolean OSLOc=  OSLO.isSelected();
        boolean OTTAWAc=  OTTAWA.isSelected();
        boolean OUAGADOUGOUc=  OUAGADOUGOU.isSelected();
        boolean PARAMARIBOc=  PARAMARIBO.isSelected();
        boolean PARISc=  PARIS.isSelected();
        boolean PHILIPSBURGc=  PHILIPSBURG.isSelected();
        boolean PLYMOUTHc=  PLYMOUTH.isSelected();
        boolean PODGORICAc=  PODGORICA.isSelected();
        boolean PRAGUEc=  PRAGUE.isSelected();
        boolean PRAIAc=  PRAIA.isSelected();
        boolean PRETORIAc=  PRETORIA.isSelected();
        boolean PRISTINAc=  PRISTINA.isSelected();
        boolean PYONGYANGc=  PYONGYANG.isSelected();
        boolean QUITOc=  QUITO.isSelected();
        boolean RABATc=  RABAT.isSelected();
        boolean RAMALLAHc=  RAMALLAH.isSelected();
        boolean RIGAc=  RIGA.isSelected();
        boolean RIYADHc=  RIYADH.isSelected();
        boolean ROMEc=  ROME.isSelected();
        boolean ROSEAUc=  ROSEAU.isSelected();
        boolean SAINTDENISc=  SAINTDENIS.isSelected();
        boolean SAINTPIERREc=  AINTPIERRE.isSelected();
        boolean SAIPANc=  SAIPAN.isSelected();
        boolean SANTIAGOc=  SANTIAGO.isSelected();
        boolean SARAJEVOc=  SARAJEVO.isSelected();
        boolean SEOULc=  SEOUL.isSelected();
        boolean SINGAPOREc=  SINGAPORE.isSelected();
        boolean SKOPJEc=  SKOPJE.isSelected();
        boolean SOFIAc=  SOFIA.isSelected();
        boolean STANLEYc=  STANLEY.isSelected();
        boolean STOCKHOLMc=  STOCKHOLM.isSelected();
        boolean SUCREc=  SUCRE.isSelected();
        boolean SUVAc=  SUVA.isSelected();
        boolean TAIPEIc=  TAIPEI.isSelected();
        boolean TALLINNc=  TALLINN.isSelected();
        boolean TASHKENTc=  TASHKENT.isSelected();
        boolean TBILISIc=  TBILISI.isSelected();
        boolean TEGUCIGALPAc=  TEGUCIGALPA.isSelected();
        boolean TEHRANc=  TEHRAN.isSelected();
        boolean THIMPHUc=  THIMPHU.isSelected();
        boolean TIRANAc=  TIRANA.isSelected();
        boolean TOKYOc=  TOKYO.isSelected();
        boolean TRIPOLIc=  TRIPOLI.isSelected();
        boolean TUNISc=  TUNIS.isSelected();
        boolean VADUZc=  VADUZ.isSelected();
        boolean VALLETTAc=  VALLETTA.isSelected();
        boolean VICTORIAc=  VICTORIA.isSelected();
        boolean VIENNAc=  VIENNA.isSelected();
        boolean VIENTIANEc=  VIENTIANE.isSelected();
        boolean VILNIUSc=  VILNIUS.isSelected();
        boolean WARSAWc=  WARSAW.isSelected();
        boolean WELLINGTONc=  WELLINGTON.isSelected();
        boolean WILLEMSTADc=  WILLEMSTAD.isSelected();
        boolean WINDHOEKc=  WINDHOEK.isSelected();
        boolean YAMOUSSOUKROc=  YAMOUSSOUKRO.isSelected();
        boolean YEREVANc=  YEREVAN.isSelected();
        boolean ZAGREBc=  ZAGREB.isSelected();

        if(ABUJA)
            Ranker.listCitiesFromUser.add("ABUJA");
        if(ACCRA)
            Ranker.listCitiesFromUser.add("ACCRA");
        if(ALGIERS)
            Ranker.listCitiesFromUser.add("ALGIERS");
        if(ALOFI)
            Ranker.listCitiesFromUser.add("ALOFI");
        if(AMMAN)
            Ranker.listCitiesFromUser.add("AMMAN");
        if(AMSTERDAM)
            Ranker.listCitiesFromUser.add("AMSTERDAM");
        if(ANKARA)
            Ranker.listCitiesFromUser.add("ANKARA");
        if(ANTANANARIV)
            Ranker.listCitiesFromUser.add("ANTANANARIV");
        if(ASHGABAT)
            Ranker.listCitiesFromUser.add("ASHGABAT");
        if(ASMARA)
            Ranker.listCitiesFromUser.add("ASMARA");
        if(ATHENS)
            Ranker.listCitiesFromUser.add("ATHENS");
        if(AVARUA)
            Ranker.listCitiesFromUser.add("AVARUA");
        if(BAGHDAD)
            Ranker.listCitiesFromUser.add("BAGHDAD");
        if(BAKU)
            Ranker.listCitiesFromUser.add("BAKU");
        if(BAMAKO)
            Ranker.listCitiesFromUser.add("BAMAKO");
        if(BANGKOK)
            Ranker.listCitiesFromUser.add("BANGKOK");
        if(BANGUI)
            Ranker.listCitiesFromUser.add("BANGUI");
        if(BANJUL)
            Ranker.listCitiesFromUser.add("BANJUL");
        if(BASSETERRE)
            Ranker.listCitiesFromUser.add("BASSETERRE");
        if(BEIJING)
            Ranker.listCitiesFromUser.add("BEIJING");
        if(BEIRUT)
            Ranker.listCitiesFromUser.add("BEIRUT");
        if(BELGRADE)
            Ranker.listCitiesFromUser.add("BELGRADE");
        if(BELMOPAN)
            Ranker.listCitiesFromUser.add("BELMOPAN");
        if(BERLIN)
            Ranker.listCitiesFromUser.add("BERLIN");
        if(BERN)
            Ranker.listCitiesFromUser.add("BERN");
        if(BISHKEK)
            Ranker.listCitiesFromUser.add("BISHKEK");
        if(BISSAU)
            Ranker.listCitiesFromUser.add("BISSAU");
        if(BRATISLAVA)
            Ranker.listCitiesFromUser.add("BRATISLAVA");
        if(BRAZZAVILLE)
            Ranker.listCitiesFromUser.add("BRAZZAVILLE");
        if(BRIDGETOWN)
            Ranker.listCitiesFromUser.add("BRIDGETOWN");
        if(BRUSSELS)
            Ranker.listCitiesFromUser.add("BRUSSELS");
        if(BUCHAREST)
            Ranker.listCitiesFromUser.add("BUCHAREST");

        if(BUDAPEST)
            Ranker.listCitiesFromUser.add("BUDAPEST");
        if(BUJUMBURA)
            Ranker.listCitiesFromUser.add("BUJUMBURA");
        if(CAIRO)
            Ranker.listCitiesFromUser.add("CAIRO");
        if(CANBERRA)
            Ranker.listCitiesFromUser.add("CANBERRA");
        if(CARACAS)
            Ranker.listCitiesFromUser.add("CARACAS");
        if(CASTRIES)
            Ranker.listCitiesFromUser.add("CASTRIES");
        if(CAYENNE)
            Ranker.listCitiesFromUser.add("CAYENNE");
        if(COLOMBO)
            Ranker.listCitiesFromUser.add("COLOMBO");
        if(CONAKRY)
            Ranker.listCitiesFromUser.add("CONAKRY");
        if(COPENHAGEN)
            Ranker.listCitiesFromUser.add("COPENHAGEN");
        if(DAKAR)
            Ranker.listCitiesFromUser.add("DAKAR");
        if(DAMASCUS)
            Ranker.listCitiesFromUser.add("DAMASCUS");
        if(DHAKA)
            Ranker.listCitiesFromUser.add("DHAKA");
        if(DILI)
            Ranker.listCitiesFromUser.add("DILI");
        if(DJIBOUTI)
            Ranker.listCitiesFromUser.add("DJIBOUTI");
        if(DODOMA)
            Ranker.listCitiesFromUser.add("DODOMA");

        if(DOHA)
            Ranker.listCitiesFromUser.add("DOHA");
        if(DOUGLAS)
            Ranker.listCitiesFromUser.add("DOUGLAS");
        if(DUBLIN)
            Ranker.listCitiesFromUser.add("DUBLIN");
        if(DUSHANBE)
            Ranker.listCitiesFromUser.add("DUSHANBE");
        if(FREETOWN)
            Ranker.listCitiesFromUser.add("FREETOWN");
        if(GABORONE)
            Ranker.listCitiesFromUser.add("GABORONE");
        if(GEORGETOWN)
            Ranker.listCitiesFromUser.add("GEORGETOWN");
        if(GIBRALTAR)
            Ranker.listCitiesFromUser.add("GIBRALTAR");
        if(GUSTAVIA)
            Ranker.listCitiesFromUser.add("GUSTAVIA");
        if(HAMILTON)
            Ranker.listCitiesFromUser.add("HAMILTON");
        if(HANOI)
            Ranker.listCitiesFromUser.add("HANOI");
        if(HARAREc)
            Ranker.listCitiesFromUser.add("HARARE");
        if(HAVANAc)
            Ranker.listCitiesFromUser.add("HAVANA");
        if(HELSINKIc)
            Ranker.listCitiesFromUser.add("HELSINKI");
        if(HONIARAc)
            Ranker.listCitiesFromUser.add("HONIARA");
        if(ISLAMABADc)
            Ranker.listCitiesFromUser.add("ISLAMABAD");

        if(JAKARTAc)
            Ranker.listCitiesFromUser.add("JAKARTA");
        if(JAMESTOWNc)
            Ranker.listCitiesFromUser.add("JAMESTOWN");
        if(JERUSALEMc)
            Ranker.listCitiesFromUser.add("JERUSALEM");
        if(JUBAc)
            Ranker.listCitiesFromUser.add("JUBA");
        if(KABULc)
            Ranker.listCitiesFromUser.add("KABUL");
        if(KAMPALAc)
            Ranker.listCitiesFromUser.add("KAMPALA");
        if(KATHMANDUc)
            Ranker.listCitiesFromUser.add("KATHMANDU");
        if(KHARTOUMc)
            Ranker.listCitiesFromUser.add("KHARTOUM");
        if(KIEVc)
            Ranker.listCitiesFromUser.add("KIEV");
        if(KIGALIc)
            Ranker.listCitiesFromUser.add("KIGALI");
        if(KINGSTONc)
            Ranker.listCitiesFromUser.add("KINGSTON");
        if(KINGSTOWNc)
            Ranker.listCitiesFromUser.add("KINGSTOWN");
        if(KINSHASAc)
            Ranker.listCitiesFromUser.add("KINSHASA");
        if(LIBREVILLEc)
            Ranker.listCitiesFromUser.add("LIBREVILLE");
        if(LILONGWEc)
            Ranker.listCitiesFromUser.add("LILONGWE");
        if(LIMAc)
            Ranker.listCitiesFromUser.add("LIMA");
        if(LISBONc)
            Ranker.listCitiesFromUser.add("LISBON");
        if(LJUBLJANAc)
            Ranker.listCitiesFromUser.add("LJUBLJANA");
        if(LOBAMBAc)
            Ranker.listCitiesFromUser.add("LOBAMBA");
        if(LONDONc)
            Ranker.listCitiesFromUser.add("LONDON");
        if(LUANDAc)
            Ranker.listCitiesFromUser.add("LUANDA");
        if(LUSAKAc)
            Ranker.listCitiesFromUser.add("LUSAKA");
        if(LUXEMBOURGc)
            Ranker.listCitiesFromUser.add("LUXEMBOURG");
        if(MADRIDc)
            Ranker.listCitiesFromUser.add("MADRID");
        if(MAJUROc)
            Ranker.listCitiesFromUser.add("MAJURO");
        if(MALABOc)
            Ranker.listCitiesFromUser.add("MALABO");
        if(MANAGUAc)
            Ranker.listCitiesFromUser.add("MANAGUA");
        if(MANAMAc)
            Ranker.listCitiesFromUser.add("MANAMA");
        if(MANILAc)
            Ranker.listCitiesFromUser.add("MANILA");
        if(MAPUTOc)
            Ranker.listCitiesFromUser.add("MAPUTO");
        if(MARIEHAMNc)
            Ranker.listCitiesFromUser.add("MARIEHAMN");


        if(MARIGOTc)
            Ranker.listCitiesFromUser.add("MARIGOT");
        if(MASERUc)
            Ranker.listCitiesFromUser.add("MASERU");
        if(MINSKc)
            Ranker.listCitiesFromUser.add("MINSK");
        if(MOGADISHUc)
            Ranker.listCitiesFromUser.add("MOGADISHU");
        if(MONACOc)
            Ranker.listCitiesFromUser.add("MONACO");
        if(MONROVIAc)
            Ranker.listCitiesFromUser.add("MONROVIA");
        if(MONTEVIDEOc)
            Ranker.listCitiesFromUser.add("MONTEVIDEO");
        if(MORONIc)
            Ranker.listCitiesFromUser.add("MORONI");
        if(MOSCOWc)
            Ranker.listCitiesFromUser.add("MOSCOW");
        if(MUSCATc)
            Ranker.listCitiesFromUser.add("MUSCAT");
        if(NAIROBIc)
            Ranker.listCitiesFromUser.add("NAIROBI");
        if(NASSAUc)
            Ranker.listCitiesFromUser.add("NASSAU");
        if(NAYPYIDAWc)
            Ranker.listCitiesFromUser.add("NAYPYIDAW");
        if(NIAMEYc)
            Ranker.listCitiesFromUser.add("NIAMEY");
        if(NICOSIAc)
            Ranker.listCitiesFromUser.add("NICOSIA");
        if(NOUAKCHOTTc)
            Ranker.listCitiesFromUser.add("NOUAKCHOTT");
        if(NUUKc)
            Ranker.listCitiesFromUser.add("NUUK");
        if(ORANJESTADc)
            Ranker.listCitiesFromUser.add("ORANJESTAD");
        if(OSLOc)
            Ranker.listCitiesFromUser.add("OSLO");
        if(OTTAWAc)
            Ranker.listCitiesFromUser.add("OTTAWA");
        if(OUAGADOUGOUc)
            Ranker.listCitiesFromUser.add("OUAGADOUGOU");
        if(PARAMARIBOc)
            Ranker.listCitiesFromUser.add("PARAMARIBO");
        if(PARISc)
            Ranker.listCitiesFromUser.add("PARIS");

        if(PHILIPSBURGc)
            Ranker.listCitiesFromUser.add("PHILIPSBURG");
        if(PLYMOUTHc)
            Ranker.listCitiesFromUser.add("PLYMOUTH");
        if(PODGORICAc)
            Ranker.listCitiesFromUser.add("PODGORICA");
        if(PRAGUEc)
            Ranker.listCitiesFromUser.add("PRAGUE");
        if(PRETORIAc)
            Ranker.listCitiesFromUser.add("PRETORIA");
        if(PRISTINAc)
            Ranker.listCitiesFromUser.add("PRISTINA");
        if(PYONGYANGc)
            Ranker.listCitiesFromUser.add("PYONGYANG");
        if(RABATc)
            Ranker.listCitiesFromUser.add("RABAT");
        if(RAMALLAHc)
            Ranker.listCitiesFromUser.add("RAMALLAH");
        if(RIGAc)
            Ranker.listCitiesFromUser.add("RIGA");
        if(ROMEc)
            Ranker.listCitiesFromUser.add("ROME");
        if(ROSEAUc)
            Ranker.listCitiesFromUser.add("ROSEAU");
        if(SAINTDENISc)
            Ranker.listCitiesFromUser.add("SAINTDENIS");
        if(SAINTPIERREc)
            Ranker.listCitiesFromUser.add("SAINTPIERRE");
        if(SAIPANc)
            Ranker.listCitiesFromUser.add("SAIPAN");
        if(SANTIAGOc)
            Ranker.listCitiesFromUser.add("SANTIAGO");
        if(SARAJEVOc)
            Ranker.listCitiesFromUser.add("SARAJEVO");
        if(SEOULc)
            Ranker.listCitiesFromUser.add("SEOUL");
        if(SINGAPOREc)
            Ranker.listCitiesFromUser.add("SINGAPORE");
        if(SKOPJEc)
            Ranker.listCitiesFromUser.add("SKOPJE");
        if(SOFIAc)
            Ranker.listCitiesFromUser.add("SOFIA");
        if(STANLEYc)
            Ranker.listCitiesFromUser.add("STANLEY");
        if(STOCKHOLMc)
            Ranker.listCitiesFromUser.add("STOCKHOLM");


        if(SUCREc)
            Ranker.listCitiesFromUser.add("SUCRE");
        if(SUVAc)
            Ranker.listCitiesFromUser.add("SUVA");
        if(TAIPEIc)
            Ranker.listCitiesFromUser.add("TAIPEI");
        if(TALLINNc)
            Ranker.listCitiesFromUser.add("TALLINN");
        if(TASHKENTc)
            Ranker.listCitiesFromUser.add("TASHKENT");
        if(TBILISIc)
            Ranker.listCitiesFromUser.add("TBILISI");
        if(TEGUCIGALPAc)
            Ranker.listCitiesFromUser.add("TEGUCIGALPA");
        if(TEHRANc)
            Ranker.listCitiesFromUser.add("TEHRAN");
        if(THIMPHUc)
            Ranker.listCitiesFromUser.add("THIMPHU");
        if(TIRANAc)
            Ranker.listCitiesFromUser.add("TIRANA");
        if(TOKYOc)
            Ranker.listCitiesFromUser.add("TOKYO");
        if(TRIPOLIc)
            Ranker.listCitiesFromUser.add("TRIPOLI");
        if(TUNISc)
            Ranker.listCitiesFromUser.add("TUNIS");
        if(VADUZc)
            Ranker.listCitiesFromUser.add("VADUZ");
        if(VALLETTAc)
            Ranker.listCitiesFromUser.add("VALLETTA");
        if(VICTORIAc)
            Ranker.listCitiesFromUser.add("VICTORIA");
        if(VIENNAc)
            Ranker.listCitiesFromUser.add("VIENNA");
        if(VIENTIANEc)
            Ranker.listCitiesFromUser.add("VIENTIANE");
        if(VILNIUSc)
            Ranker.listCitiesFromUser.add("VILNIUS");
        if(WARSAWc)
            Ranker.listCitiesFromUser.add("WARSAW");
        if(WELLINGTONc)
            Ranker.listCitiesFromUser.add("WELLINGTON");
        if(WILLEMSTADc)
            Ranker.listCitiesFromUser.add("WILLEMSTAD");
        if(WINDHOEKc)
            Ranker.listCitiesFromUser.add("WINDHOEK");
        if(YAMOUSSOUKROc)
            Ranker.listCitiesFromUser.add("YAMOUSSOUKRO");
        if(YEREVANc)
            Ranker.listCitiesFromUser.add("YEREVAN");
        if(ZAGREBc)
            Ranker.listCitiesFromUser.add("ZAGREB");
        if(APIA)
            Ranker.listCitiesFromUser.add("APIA");
        if(PRAIAc)
            Ranker.listCitiesFromUser.add("PRAIA");
        if(QUITOc)
            Ranker.listCitiesFromUser.add("QUITO");
        if(RIYADHc)
            Ranker.listCitiesFromUser.add("RIYADH");


        for(int i=0;i<Ranker.listCitiesFromUser.size();i++)
            System.out.println(Ranker.listCitiesFromUser.get(i));

        Stage stage = (Stage) chooseButton.getScene().getWindow();
        stage.close();
    }




}
