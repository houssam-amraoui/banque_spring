package com.isga.pr2.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.isga.pr2.model.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Couleurs
    private static final BaseColor BLEU_BANQUE   = new BaseColor(0, 71, 171);
    private static final BaseColor BLEU_CLAIR    = new BaseColor(235, 242, 255);
    private static final BaseColor VERT_DEPOT    = new BaseColor(0, 128, 0);
    private static final BaseColor ROUGE_RETRAIT = new BaseColor(180, 0, 0);
    private static final BaseColor GRIS_ENTETE   = new BaseColor(50, 50, 50);
    private static final BaseColor BLANC         = BaseColor.WHITE;
    private static final BaseColor GRIS_CLAIR    = new BaseColor(245, 245, 245);

    // Polices
    private Font fontTitre;
    private Font fontSousTitre;
    private Font fontSection;
    private Font fontNormal;
    private Font fontNormalBold;
    private Font fontSmall;
    private Font fontDepot;
    private Font fontRetrait;
    private Font fontTableHeader;

    public byte[] genererReleve(Client client, ComptesBancaires compte, List<Operation> operations)
            throws DocumentException {

        initFonts();

        Document document = new Document(PageSize.A4, 40, 40, 60, 60);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);

        writer.setPageEvent(new HeaderFooterEvent(client, compte));

        document.open();

        addBanqueHeader(document);
        addTitreReleve(document, compte);

        addSection(document, "Informations Client");
        addInfosClient(document, client);

        addSection(document, "Détails du Compte");
        addInfosCompte(document, compte);

        addSection(document, "Historique des Opérations");
        addTableauOperations(document, operations);

        addSection(document, "Récapitulatif");
        addRecapitulatif(document, compte, operations);

        document.close();
        return out.toByteArray();
    }

    private void initFonts() throws DocumentException {
        fontTitre       = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD,   BLEU_BANQUE);
        fontSousTitre   = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, GRIS_ENTETE);
        fontSection     = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD,   BLANC);
        fontNormal      = new Font(Font.FontFamily.HELVETICA, 9,  Font.NORMAL, GRIS_ENTETE);
        fontNormalBold  = new Font(Font.FontFamily.HELVETICA, 9,  Font.BOLD,   GRIS_ENTETE);
        fontSmall       = new Font(Font.FontFamily.HELVETICA, 7,  Font.NORMAL, BaseColor.GRAY);
        fontDepot       = new Font(Font.FontFamily.HELVETICA, 9,  Font.BOLD,   VERT_DEPOT);
        fontRetrait     = new Font(Font.FontFamily.HELVETICA, 9,  Font.BOLD,   ROUGE_RETRAIT);
        fontTableHeader = new Font(Font.FontFamily.HELVETICA, 9,  Font.BOLD,   BLANC);
    }

    private void addBanqueHeader(Document doc) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 2});
        table.setSpacingAfter(4);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.setPadding(4);
        Paragraph nomBanque = new Paragraph("🏦 BANQUE MAROC DIGITAL", fontTitre);
        left.addElement(nomBanque);
        Paragraph slogan = new Paragraph("Votre partenaire financier de confiance", fontSousTitre);
        left.addElement(slogan);
        table.addCell(left);

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setPadding(4);
        Font fAdresse = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, GRIS_ENTETE);
        right.addElement(new Paragraph("123, Avenue Mohammed V, Rabat", fAdresse));
        right.addElement(new Paragraph("Tél : +212 537 000 000", fAdresse));
        right.addElement(new Paragraph("contact@banquemaroc.ma", fAdresse));
        right.addElement(new Paragraph("www.banquemaroc.ma", fAdresse));
        table.addCell(right);

        doc.add(table);

        // Ligne séparatrice bleue
        LineSeparator line = new LineSeparator(2, 100, BLEU_BANQUE, Element.ALIGN_CENTER, -2);
        doc.add(new Chunk(line));
        doc.add(Chunk.NEWLINE);
    }

    private void addTitreReleve(Document doc, ComptesBancaires compte) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(6);
        t.setSpacingAfter(10);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BLEU_BANQUE);
        cell.setPadding(10);
        cell.setBorder(Rectangle.NO_BORDER);

        Font fTitre = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BLANC);
        Font fSub   = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, new BaseColor(200, 220, 255));

        cell.addElement(new Paragraph("RELEVÉ DE COMPTE BANCAIRE", fTitre));
        cell.addElement(new Paragraph("Compte N° " + compte.getNumeroCompte() +
                " — " + compte.getTypeCompte(), fSub));
        cell.addElement(new Paragraph("Édité le " +
                java.time.LocalDateTime.now().format(DATE_FMT), fSub));
        t.addCell(cell);
        doc.add(t);
    }

    private void addSection(Document doc, String titre) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(8);
        t.setSpacingAfter(4);

        PdfPCell cell = new PdfPCell(new Phrase("  " + titre.toUpperCase(), fontSection));
        cell.setBackgroundColor(BLEU_BANQUE);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        t.addCell(cell);
        doc.add(t);
    }

    private void addInfosClient(Document doc, Client client) throws DocumentException {
        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});
        t.setSpacingAfter(6);

        addInfoRow(t, "Nom complet", client.getNomComplet(), "Email", client.getEmail());
        addInfoRow(t, "Téléphone",
                client.getTelephone() != null ? client.getTelephone() : "—",
                "Adresse",
                client.getAdresse() != null ? client.getAdresse() : "—");
        doc.add(t);
    }

    private void addInfosCompte(Document doc, ComptesBancaires compte) throws DocumentException {
        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});
        t.setSpacingAfter(6);

        addInfoRow(t, "N° Compte", compte.getNumeroCompte(),
                "Type", compte.getTypeCompte());
        addInfoRow(t, "Date ouverture",
                compte.getDateCreation() != null ? compte.getDateCreation().format(DATE_ONLY) : "—",
                "Info spécifique", compte.getInfosSpecifiques());
        addInfoRow(t, "Solde actuel",
                String.format("%.2f MAD", compte.getSolde()), "", "");
        doc.add(t);
    }

    private void addInfoRow(PdfPTable t, String label1, String val1, String label2, String val2) {
        addLabelCell(t, label1);
        addValueCell(t, val1);
        addLabelCell(t, label2);
        addValueCell(t, val2);
    }

    private void addLabelCell(PdfPTable t, String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, fontNormalBold));
        c.setBackgroundColor(BLEU_CLAIR);
        c.setPadding(5);
        c.setBorderColor(BaseColor.LIGHT_GRAY);
        t.addCell(c);
    }

    private void addValueCell(PdfPTable t, String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, fontNormal));
        c.setPadding(5);
        c.setBorderColor(BaseColor.LIGHT_GRAY);
        t.addCell(c);
    }

    private void addTableauOperations(Document doc, List<Operation> operations) throws DocumentException {
        if (operations.isEmpty()) {
            doc.add(new Paragraph("Aucune opération enregistrée.", fontNormal));
            return;
        }

        PdfPTable t = new PdfPTable(5);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{2.5f, 1.2f, 1.5f, 2.5f, 1.8f});
        t.setHeaderRows(1);
        t.setSpacingAfter(6);

        String[] headers = {"Date & Heure", "Type", "Montant (MAD)", "Description", "Solde après (MAD)"};
        for (String h : headers) {
            PdfPCell hCell = new PdfPCell(new Phrase(h, fontTableHeader));
            hCell.setBackgroundColor(BLEU_BANQUE);
            hCell.setPadding(6);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hCell.setBorder(Rectangle.NO_BORDER);
            t.addCell(hCell);
        }

        List<Operation> ops = new java.util.ArrayList<>(operations);
        java.util.Collections.reverse(ops);

        double[] soldesApres = new double[ops.size()];
        double s = 0;
        for (int i = 0; i < ops.size(); i++) {
            Operation op = ops.get(i);
            s += op.getType() == TypeOperation.DEPOT ? op.getMontant() : -op.getMontant();
            soldesApres[i] = s;
        }

        java.util.Collections.reverse(ops);
        double[] soldesDesc = new double[ops.size()];
        for (int i = 0; i < soldesApres.length; i++) {
            soldesDesc[i] = soldesApres[soldesApres.length - 1 - i];
        }

        boolean alt = false;
        for (int i = 0; i < ops.size(); i++) {
            Operation op = ops.get(i);
            BaseColor bg = alt ? GRIS_CLAIR : BLANC;
            alt = !alt;

            addTableCell(t, op.getDateOperation().format(DATE_FMT), fontNormal, bg, Element.ALIGN_CENTER);

            Font fType = op.getType() == TypeOperation.DEPOT ? fontDepot : fontRetrait;
            addTableCell(t, op.getType().getLibelle(), fType, bg, Element.ALIGN_CENTER);

            Font fMontant = op.getType() == TypeOperation.DEPOT ? fontDepot : fontRetrait;
            String sign = op.getType() == TypeOperation.DEPOT ? "+" : "-";
            addTableCell(t, sign + String.format("%.2f", op.getMontant()), fMontant, bg, Element.ALIGN_RIGHT);

            addTableCell(t, op.getDescription() != null ? op.getDescription() : "—", fontNormal, bg, Element.ALIGN_LEFT);
            addTableCell(t, String.format("%.2f", soldesDesc[i]), fontNormalBold, bg, Element.ALIGN_RIGHT);
        }

        doc.add(t);
    }

    private void addTableCell(PdfPTable t, String text, Font font, BaseColor bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(bg);
        c.setPadding(5);
        c.setHorizontalAlignment(align);
        c.setBorderColor(BaseColor.LIGHT_GRAY);
        t.addCell(c);
    }

    private void addRecapitulatif(Document doc, ComptesBancaires compte, List<Operation> operations)
            throws DocumentException {

        double totalDepots   = operations.stream()
                .filter(o -> o.getType() == TypeOperation.DEPOT)
                .mapToDouble(Operation::getMontant).sum();
        double totalRetraits = operations.stream()
                .filter(o -> o.getType() == TypeOperation.RETRAIT)
                .mapToDouble(Operation::getMontant).sum();
        long nbDepots   = operations.stream().filter(o -> o.getType() == TypeOperation.DEPOT).count();
        long nbRetraits = operations.stream().filter(o -> o.getType() == TypeOperation.RETRAIT).count();

        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(60);
        t.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.setSpacingAfter(10);

        addRecapRow(t, "Nombre de dépôts",   String.valueOf(nbDepots),   false);
        addRecapRow(t, "Total dépôts",        String.format("+ %.2f MAD", totalDepots),  false);
        addRecapRow(t, "Nombre de retraits",  String.valueOf(nbRetraits), false);
        addRecapRow(t, "Total retraits",      String.format("- %.2f MAD", totalRetraits), false);
        addRecapRow(t, "Solde final",         String.format("%.2f MAD", compte.getSolde()), true);

        doc.add(t);

        Font fMention = new Font(Font.FontFamily.HELVETICA, 7, Font.ITALIC, BaseColor.GRAY);
        doc.add(new Paragraph("Ce relevé est un document officiel. Toute réclamation doit être soumise " +
                "dans un délai de 30 jours à compter de la date d'édition.", fMention));
    }

    private void addRecapRow(PdfPTable t, String label, String valeur, boolean highlight) {
        BaseColor bg = highlight ? BLEU_BANQUE : BLANC;
        Font fl = highlight ? new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BLANC)
                : fontNormalBold;
        Font fv = highlight ? new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BLANC)
                : fontNormal;

        PdfPCell cl = new PdfPCell(new Phrase(label, fl));
        cl.setBackgroundColor(bg);
        cl.setPadding(6);
        cl.setBorderColor(BaseColor.LIGHT_GRAY);
        t.addCell(cl);

        PdfPCell cv = new PdfPCell(new Phrase(valeur, fv));
        cv.setBackgroundColor(bg);
        cv.setPadding(6);
        cv.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cv.setBorderColor(BaseColor.LIGHT_GRAY);
        t.addCell(cv);
    }

    private class HeaderFooterEvent extends PdfPageEventHelper {
        private final Client client;
        private final ComptesBancaires compte;

        HeaderFooterEvent(Client client, ComptesBancaires compte) {
            this.client  = client;
            this.compte  = compte;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Font fFooter = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.GRAY);

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Client : " + client.getNomComplet() +
                            " | Compte : " + compte.getNumeroCompte(), fFooter),
                    document.left(), document.bottom() - 10, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    new Phrase("Page " + writer.getPageNumber() +
                            " | CONFIDENTIEL", fFooter),
                    document.right(), document.bottom() - 10, 0);
        }
    }
}
