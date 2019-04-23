import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.io.*;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.*;


public class RecommendationEngine {
    static final String DB_URL = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
    static final String USER = "root";
    static final String PASS = "larina11";

    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            Statement stmt = conn.createStatement();

            //Create database
            String sql = "DROP DATABASE IF EXISTS MovieEngine";
            stmt.executeUpdate(sql);

            sql = "CREATE DATABASE MovieEngine";
            stmt.executeUpdate(sql);
            System.out.println("Database created.");

            //Use database
            sql = "USE MovieEngine";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE Directors(id INT, name VARCHAR(45), PRIMARY KEY (id))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE Keywords(id INT, name VARCHAR(60), PRIMARY KEY (id))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE Movies(movieID INT, movieName VARCHAR(100), releaseDate DATE, rating FLOAT, popularity FLOAT, voteCount INT, PRIMARY KEY (movieid))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE Genres(id INT, name VARCHAR(45), PRIMARY KEY (id))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE Casted(castID INT, charInMovie VARCHAR(300), creditID VARCHAR(45), gender INT, id INT, name VARCHAR(100), orderShown INT, PRIMARY KEY (id))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE MovieCast(castID INT, charInMovie VARCHAR(300), creditID VARCHAR(45), gender INT, id INT, name VARCHAR(100), orderShown INT, movieID INT, PRIMARY KEY (id, movieID), FOREIGN KEY (movieID) REFERENCES Movies(movieID))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE MovieGenre(id INT, name VARCHAR(45), movieID INT, PRIMARY KEY (id, movieID), FOREIGN KEY (movieID) REFERENCES Movies(movieID))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE MovieKeyword(id INT, name VARCHAR(60), movieID INT, PRIMARY KEY (id, movieID), FOREIGN KEY (movieID) REFERENCES Movies(movieID))";
            stmt.executeUpdate(sql);

            System.out.println("Tables created.");

            System.out.print("Importing ");

            sql = "INSERT INTO Directors VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.clearParameters();
            String csv = "./director/directors.csv";
            BufferedReader br = new BufferedReader(new FileReader(csv));
            String line = "";
            System.out.print("directors... ");
            Boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                line = line.replace("\"", "");
                String[] directors = line.split(",");
                pstmt.setInt(1, Integer.parseInt(directors[0]));
                pstmt.setString(2, directors[1]);
                pstmt.executeUpdate();
            }
            sql = "INSERT INTO Keywords VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.clearParameters();
            csv = "./keywords entity/keywords.csv";
            br = new BufferedReader(new FileReader(csv));
            System.out.print("keywords... ");
            first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                line = line.replace("\"", "");
                String[] keywords = line.split(",");
                pstmt.setInt(1, Integer.parseInt(keywords[0]));
                pstmt.setString(2, keywords[1]);
                pstmt.executeUpdate();
            }

            sql = "INSERT INTO Movies VALUES(?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.clearParameters();
            csv = "./movie entity/movies.csv";
            br = new BufferedReader(new FileReader(csv));
            System.out.print("movies... ");
            first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                //line = line.replace("\"", "");
                String[] movies = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                pstmt.setInt(1, Integer.parseInt(movies[0]));
                pstmt.setString(2, movies[1]);
                movies[2] = movies[2].replace('/', '-');
                SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
                java.util.Date date = sdf1.parse(movies[2]);
                java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());
                pstmt.setDate(3, sqlStartDate);
                pstmt.setFloat(4, Float.parseFloat(movies[3]));
                pstmt.setFloat(5, Float.parseFloat(movies[4]));
                pstmt.setInt(6, Integer.parseInt(movies[5]));
                pstmt.executeUpdate();
            }

            sql = "INSERT INTO Genres VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.clearParameters();
            csv = "./genre entity/genres.csv";
            br = new BufferedReader(new FileReader(csv));
            System.out.print("genres... ");
            first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                line = line.replace("\"", "");
                String[] genres = line.split(",");
                pstmt.setInt(1, Integer.parseInt(genres[0]));
                pstmt.setString(2, genres[1]);
                pstmt.executeUpdate();
            }

            sql = "INSERT INTO Casted VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.clearParameters();
            csv = "./cast entity/castparsed.csv";
            br = new BufferedReader(new FileReader(csv));
            System.out.print("cast... ");
            first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                //line = line.replace("\"", "");
                String[] cast = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                pstmt.setInt(1, Integer.parseInt(cast[0]));
                pstmt.setString(2, cast[1]);
                pstmt.setString(3, cast[2]);
                pstmt.setInt(4, Integer.parseInt(cast[3]));
                pstmt.setInt(5, Integer.parseInt(cast[4]));
                pstmt.setString(6, cast[5]);
                pstmt.setInt(7, Integer.parseInt(cast[6]));
                pstmt.executeUpdate();
            }

            sql = "INSERT INTO MovieCast VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.clearParameters();
            csv = "./movie to cast (1-many)/movieMatchesCast.csv";
            br = new BufferedReader(new FileReader(csv));
            System.out.print("movie to cast... ");
            first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                //line = line.replace("\"", "");
                String[] cast = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                pstmt.setInt(1, Integer.parseInt(cast[0]));
                pstmt.setString(2, cast[1]);
                pstmt.setString(3, cast[2]);
                pstmt.setInt(4, Integer.parseInt(cast[3]));
                pstmt.setInt(5, Integer.parseInt(cast[4]));
                pstmt.setString(6, cast[5]);
                pstmt.setInt(7, Integer.parseInt(cast[6]));
                pstmt.setInt(8, Integer.parseInt(cast[7]));
                pstmt.executeUpdate();
            }

            sql = "INSERT INTO MovieGenre VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.clearParameters();
            csv = "./movie to genre (1-many)/genreMatchesMovie.csv";
            br = new BufferedReader(new FileReader(csv));
            System.out.print("movie to genre... ");
            first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] mc = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                pstmt.setInt(1, Integer.parseInt(mc[0]));
                pstmt.setString(2, mc[1]);
                pstmt.setInt(3, Integer.parseInt(mc[2]));
                pstmt.executeUpdate();
            }

            sql = "INSERT INTO MovieKeyword VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.clearParameters();
            csv = "./movie to keyword (1-many)/keywordsMatchesMovie.csv";
            br = new BufferedReader(new FileReader(csv));
            System.out.print("movie to keyword... ");
            first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] mk = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                pstmt.setInt(1, Integer.parseInt(mk[0]));
                pstmt.setString(2, mk[1]);
                pstmt.setInt(3, Integer.parseInt(mk[2]));
                pstmt.executeUpdate();
            }

            System.out.println("Database fully imported.");
            pstmt.close();

            JFrame f = new JFrame("Recommendation Engine");

            f.setSize(250, 250);
            f.setLocation(300, 200);
            JPanel pane1 = new JPanel(new FlowLayout());
            JLabel label1 = new JLabel();
            label1.setText("Enter movie: ");
            JTextField textField = new JTextField(10);
            JButton b = new JButton("Search");
            pane1.add(label1);
            pane1.add(textField);
            pane1.add(b);
            f.getContentPane().add(BorderLayout.CENTER, pane1);
            final JTextArea textArea = new JTextArea(10, 40);
            f.getContentPane().add(BorderLayout.SOUTH, textArea);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(45);
                }
            });


            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String selection = textField.getText();
                    try {
                        String sequel = "SELECT movieid FROM movies WHERE movieName = \"" + selection + "\"";
                        ResultSet myRS = stmt.executeQuery(sequel);

                        if (!myRS.next()) {
                            textArea.append("I am sorry, I could not find a movie named " + selection + " in my database.");
                        } else {
                            String movieid = myRS.getString("movieid");
                            sequel = "select t1.matches, t1.movieid, t1.moviename from (select count(*) as matches, movies.movieid, movies.movieName from movies inner join moviegenre on movies.movieid = moviegenre.movieid where id in (select moviegenre.id from moviegenre, movies where movies.movieid = " + movieid + " and movies.movieid = moviegenre.movieid) and movies.movieid != " + movieid + " group by movies.movieid order by matches desc, movies.rating desc, movies.popularity desc) t1 inner join (select count(*) as matches, movies.movieid, movies.movieName from movies inner join moviekeyword on movies.movieid = moviekeyword.movieid where id in (select moviekeyword.id from moviekeyword, movies where movies.movieid = " + movieid + " and movies.movieid = moviekeyword.movieid) and movies.movieid != " + movieid + " group by movies.movieid order by matches desc, movies.rating desc, movies.popularity) t2 on (t1.movieid = t2.movieid) limit 5;\n";
                            myRS = stmt.executeQuery(sequel);
                            if (!myRS.next())
                                textArea.append("No recommended movies. You probably shouldn't be here.");
                            else {
                                do {
                                    textArea.append(myRS.getString("moviename") + "\n");
                                } while (myRS.next());
                            }
                        }
                    }catch(SQLException se){
                        System.out.println("SQL Exception.");
                    }
                }
            });

            f.setVisible(true);


                    /*while(notDone){

                        switch(select) {
                            case 1:
                                System.out.println("Please type year: ");
                                int year = input.nextInt();
                                while(String.valueOf(year).length() != 4){
                                    System.out.println("Invalid year, try again.");
                                    year = input.nextInt();
                                }
                                System.out.println("Please type month in digits: ");
                                int month = input.nextInt();
                                while(String.valueOf(month).length() > 2){
                                    System.out.println("Invalid month, try again.");
                                    month = input.nextInt();
                                }

                                sql = "SELECT real_name, tag, nationality FROM Players WHERE YEAR(birthday) = " + year + " AND Month(birthday) = " + month;

                                ResultSet myRS = stmt.executeQuery(sql);

                                if(!myRS.next()){
                                    System.out.println("Query returned no results");
                                }else {
                                    System.out.println("+------------------------------------+-----------------+---------------+");
                                    System.out.printf("| %-34s | %-15s | %-13s |\n", "Real Name", "Tag", "Nationality");
                                    System.out.println("+------------------------------------+-----------------+---------------+");
                                    do {
                                        System.out.printf("| %-34s | %-15s | %-13s |\n", myRS.getString("real_name"), myRS.getString("tag"), myRS.getString("nationality"));
                                    } while (myRS.next());
                                    System.out.println("+------------------------------------+-----------------+---------------+");
                                }

                                break;
                            case 2:
                                System.out.println("Please type player id:");
                                int player_id = input.nextInt();
                                System.out.println("Please type team id:");
                                int team_id = input.nextInt();

                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDateTime now = LocalDateTime.now();

                                sql = "SELECT * FROM Members WHERE player = " + player_id + " and end_date is null";
                                myRS = stmt.executeQuery(sql);
                                if (myRS.next()) {
                                    if (myRS.getInt("team") == team_id) {
                                        System.out.println("Player is already a part of this team.");
                                        break;
                                    } else {
                                        System.out.println("Updating old team...");
                                        stmt = conn.createStatement();
                                        sql = "UPDATE Members SET end_date = '" + dtf.format(now) + "' WHERE player = " + player_id + " AND team = " + myRS.getInt("team");
                                        stmt.executeUpdate(sql);
                                    }
                                } else {
                                    System.out.println("Invalid player id or team id, please try again.");
                                    break;
                                }

                                System.out.println("Inserting player into members...");

                                sql = "INSERT INTO Members VALUES (?, ?, ?, ?)";
                                pstmt = conn.prepareStatement(sql);
                                pstmt.clearParameters();
                                pstmt.setInt(1, player_id);
                                pstmt.setInt(2, team_id);
                                pstmt.setString(3, dtf.format(now));
                                pstmt.setString(4, null);
                                pstmt.executeUpdate();

                                break;
                            case 3:
                                System.out.println("Please type nationality: ");
                                String nationality = input.next();
                                while(nationality.length() != 2){
                                    System.out.println("Invalid nationality, try again.");
                                    nationality = input.next();
                                }
                                System.out.println("Please type year: ");
                                year = input.nextInt();
                                while(String.valueOf(year).length() != 4){
                                    System.out.println("Invalid year, try again.");
                                    year = input.nextInt();
                                }

                                sql = "SELECT real_name, birthday FROM Players WHERE YEAR(birthday) = " + year + " AND nationality = '" + nationality + "'";
                                myRS = stmt.executeQuery(sql);
                                if(!myRS.next()){
                                    System.out.println("Query returned no results");
                                }else {
                                    System.out.println("+------------------------------------+--------------+");
                                    System.out.printf("| %-34s | %-12s |\n", "Real Name", "Birthday");
                                    System.out.println("+------------------------------------+--------------+");
                                    do {
                                        System.out.printf("| %-34s | %-12s |\n", myRS.getString("real_name"), myRS.getString("birthday"));
                                    } while (myRS.next());
                                    System.out.println("+------------------------------------+--------------+");
                                }

                                break;
                            case 4:
                                sql = "select p.tag, p.game_race from players p where p.tag IN ( select p.tag from players p, earnings e, tournaments t where p.player_id = e.player and e.tournament = t.tournament_id and e.position = 1 and t.major = 1 and t.region = \"AM\" ) and p.tag IN ( select p.tag from players p, earnings e, tournaments t where p.player_id = e.player and e.tournament = t.tournament_id and e.position = 1 and t.major = 1 and t.region = \"KR\" ) and p.tag IN ( select p.tag from players p, earnings e, tournaments t where p.player_id = e.player and e.tournament = t.tournament_id and e.position = 1 and t.major = 1 and t.region = \"EU\" )";
                                myRS = stmt.executeQuery(sql);
                                System.out.println("+------------+-------------+");
                                System.out.printf("| %-10s | %-11s |\n", "Tag", "Game Race");
                                System.out.println("+------------+-------------+");

                                while(myRS.next()){
                                    System.out.printf("| %-10s | %-11s |\n", myRS.getString("tag"), myRS.getString("game_race"));
                                }
                                System.out.println("+------------+-------------+");
                                break;
                            case 5:
                                sql = "select players.tag, players.real_name, max(members.end_date) from teams, players, members where teams.name = \"ROOT Gaming\" AND teams.team_id = members.team AND members.player = players.player_id AND members.end_date is not null group by players.tag, players.real_name";
                                myRS = stmt.executeQuery(sql);

                                System.out.println("+-----------------+------------------------------------+-------------------+");
                                System.out.printf("| %-15s | %-34s | %-17s |\n", "Tag", "Real Name", "Latest End Date");
                                System.out.println("+-----------------+------------------------------------+-------------------+");
                                while(myRS.next()){
                                    System.out.printf("| %-15s | %-34s | %-17s |\n", myRS.getString("tag"), myRS.getString("real_name"), myRS.getString("max(members.end_date)"));
                                }
                                System.out.println("+-----------------+------------------------------------+-------------------+");
                                break;
                            case 6:

                                break;
                            case 7:
                                break;
                            case 8:
                                System.out.println("Exiting...");
                                notDone = false;
                                break;
                            default:
                                System.out.println("Invalid query option.");
                        }
                        if(notDone) {
                            System.out.println("\nPlease type in your next query choice (1-8): ");
                            select = 0;
                        }else{
                            break;
                        }
                    }
                     */

            sql = "DROP DATABASE MovieEngine";

            stmt.executeUpdate(sql);

            System.out.println("Database dropped.");

        } catch (SQLException se) {
            System.out.println("SQL Exception.");
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        System.out.println("Run complete. Shutting down.");
    }

}