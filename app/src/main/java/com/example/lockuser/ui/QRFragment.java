package com.example.lockuser.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.lockuser.AuthenticationViewModel;
import com.example.lockuser.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.glxn.qrgen.android.QRCode;

public class QRFragment extends Fragment {

    private Button buttonSignOut;
    private NavController navController;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_qr, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        final AuthenticationViewModel authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        buttonSignOut = root.findViewById(R.id.button_sign_out);

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticationViewModel.signOut();
                navController.navigate(R.id.action_QRFragment_to_loginFragment);
            }
        });

        final String jwt = sharedPreferences.getString("access_token", null);
        final ImageView imageView = root.findViewById(R.id.image_view_qr_code);

        Bitmap bitmap = QRCode.from(jwt).withSize(1024, 1024).bitmap();
        imageView.setImageBitmap(bitmap);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
    }
}
