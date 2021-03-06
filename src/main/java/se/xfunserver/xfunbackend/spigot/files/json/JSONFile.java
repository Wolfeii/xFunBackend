package se.xfunserver.xfunbackend.spigot.files.json;

import com.github.enerccio.gson.JsonHelper;
import com.github.enerccio.gson.builders.ValueBuilder;
import com.github.enerccio.gson.visitors.JsonElementType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JSONFile {

    private File file;
    private final Gson gson;
    private final String fileName;

    public JSONFile(String folder, String fileName){
        this.fileName = fileName;
        this.gson = new Gson();

        try {
            File f = new File(Core.getPlugin().getDataFolder() + File.separator + folder);
            if (!f.exists()){
                f.mkdir();
            }
            this.file = new File(f.getAbsolutePath() + File.separator + fileName + ".json");
            if (!exists()) {
                if (!this.file.createNewFile()){
                    xFunLogger.error("JSON file '" + fileName + "' not created! FEL!");
                }
            }
        }catch(Exception e){
            xFunLogger.debug("Ett fel uppstod med att skapa JSON file.");
            xFunLogger.error(e.getMessage());
        }
    }

    public boolean exists(){
        return this.file != null && this.file.exists();
    }

    public JSONFile write(ValueBuilder jsonBuilder){
        this.finish(jsonBuilder.toJson());
        return this;
    }

    private void finish(String data){
        try {
            FileWriter fileWriter = new FileWriter(this.file);
            if (data != null)
                fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
        }catch(Exception e){
            xFunLogger.error("Error while finishing JSON conversion for '"+this.fileName+".json'");
            xFunLogger.error(e.getMessage());
        }
    }

    public void finish(){
        this.finish(null);
    }

    private String get() throws NullPointerException{
        try{
            return this.gson.toJson(gson.fromJson(new FileReader(this.file), Object.class));
        }catch (FileNotFoundException e){
            xFunLogger.error("Error while parsing JSON.");
            xFunLogger.error(e.getMessage());
            return null;
        }
    }

    public JsonElement hardGet(String pattern) throws ParseException {
        return JsonHelper.getFirst(this.file.getAbsolutePath(), pattern);
    }

    public boolean contains(String pattern, JsonHelper.IJsonPredicate jsonPredicate){
        try{
            if (jsonPredicate == null){
                return JsonHelper.getFirst(this.get(), pattern) != null;
            }
            return JsonHelper.getFirst(this.get(), pattern, jsonPredicate) != null;
        }catch (ParseException exception){
            return false;
        }
    }

    public boolean contains(String pattern){
        return this.contains(pattern, null);
    }

    public JsonElement getAs(String path) {
        return this.all(path).next();
    }

    public Iterator<JsonElement> all(String path){
        try{
            List<JsonElement> list = JsonHelper.getAll(this.get(), path);
            Collections.reverse(list); // Don't ask why, idk.
            return list.iterator();
        }catch (ParseException e){
            xFunLogger.error("Error while parsing JSON.");
            xFunLogger.error(e.getMessage());
            return null;
        }
    }

    private Class<?> getElementClass(JsonElementType jsonElementType){
        switch (jsonElementType){
            case ARRAY: return JsonArray.class;
            case OBJECT: return JsonObject.class;
            case NULL: return null;
            default: return JsonElement.class;
        }
    }

}