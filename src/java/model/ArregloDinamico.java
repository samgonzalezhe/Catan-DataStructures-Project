package com.mycompany.catan;

public class ArregloDinamico<T> {
    private T[] elements;
    private int size;
    private int length;

    public ArregloDinamico(int m){
        this.length = 0;
        this.size = m;
        this.elements = (T[]) new Object[size];
    }

    private void grow(){
        if (length == size){
            int m = (int)(size*1.618);
            T[] newArray = (T[]) new Object[m];
            for (int i = 0; i < size; i++) {
                newArray[i] = elements[i];
            }
            elements = newArray;
            size = m;
        }
    }

    public boolean insert(int index, T data){ 
        if (0 <= index && index <= length){
            grow();
            for(int i = length; i > index; i--){
                elements[i] = elements[i - 1];
            }
            elements[index] = data;
            length++;
            return true;
        }
        return false;
    }

    public void append(T data){ 
        insert(length, data);
    }

    private void shrink(){
        if(length <= (int)(size/(1.618*1.618)) && size > 20){
            int m = (int) (size/1.618);
            T[] newArray = (T[]) new Object[m];
            
            for(int i = 0; i < length; i++){ 
                newArray[i] = elements[i];
            }
            elements = newArray;
            size = m;
        }
    }

    public boolean remove(int index){ 
        if (0 <= index && index < length){
            length--;
            for(int i = index; i < length; i++){
                elements[i] = elements[i + 1];
            }
            shrink();
            return true;
        }
        return false;
    }

    private boolean del(){
        return remove(length-1);
    }

    public int size(){ 
        return length;
    }

    
    @Override
    public String toString(){
        if (length == 0) return "[]";

        StringBuilder sb = new StringBuilder("[");
        for(int i = 0; i < length - 1; i++){
            sb.append(elements[i]).append(", ");
        }
        sb.append(elements[length - 1]).append("]");
        return sb.toString();
    }
    
    
    public T get(int index){
        if(index >= 0 && index < length){
            return elements[index];
        }
        return null;
    }
    
    
    public void set(int index, T data){
        if(index >= 0 && index < length){
            elements[index] = data;
        }
    }
}
